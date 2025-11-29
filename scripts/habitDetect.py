import cv2
import numpy as np
import pyheif
from PIL import Image
import matplotlib.pyplot as plt
from pathlib import Path
import sys
import json

# ------------------------------------
# load image and resize
# ------------------------------------
def load_image(path):
    path = Path(path)

    if path.suffix.lower() == ".heic":
        heif = pyheif.read(path)
        img = Image.frombytes(
            heif.mode,
            heif.size,
            heif.data,
            "raw",
            heif.mode,
            heif.stride,
        )
        img = np.array(img)
        img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
    else:
        img = cv2.imread(str(path))

    if img is None or img.size == 0:
        raise ValueError("Error while loading")

    return img

def resize(img, max_w=1200):
    h, w = img.shape[:2]
    if w <= max_w:
        return img
    scale = max_w / w
    return cv2.resize(img, (int(w*scale), int(h*scale)))

# ------------------------------------
# Preprocessing: Gray → Blur → Adaptive Threshold → Canny
# ------------------------------------
def preprocess(img):
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    blurred = cv2.GaussianBlur(gray, (13, 13), 0)
    binary = cv2.adaptiveThreshold(
        blurred,
        255,
        cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
        cv2.THRESH_BINARY_INV,
        11,
        2
    )
    edges = cv2.Canny(binary, 50, 150)
    return gray, binary, edges


# ------------------------------------
# Hough Lines → vertical/horizontal → sort
# ------------------------------------

# stretch a short line all over the picture
def extend_line(x1, y1, x2, y2, W, H):
    if abs(x1 - x2) < abs(y1 - y2):
        # line has very less change in x compared to y -> likely vertical line
        return (x1, 0, x1, H)
    else:
        return (0, y1, W, y1)


def cluster_lines(lines, threshold=12):
    """
    Group lines by there position with a threshold and calcualte the mean

    lines: list of lines [(x1,y1,x2,y2), ...]
    threshold: max distance between lines to form a cluster
    """
    if not lines:
        return []

    # i only need one point x1, or y2 because i assume that the lines are straight
    # why check for lower 5?
    # -> if x1 ≈ x2 then it is a vertical line. 5 as tolerance as the lines aren't exactly straigth
    coords = []
    for (x1, y1, x2, y2) in lines:
        if abs(x1 - x2) < 5:
            coords.append(x1) #vertical
        else:
            coords.append(y1) #horizontal

    # sort because nearby table lines must appear consecutively
    coords = sorted(coords)
    clusters = [[coords[0]]]

    for c in coords[1:]:
        # coordinate is close to the last one, put it in the same cluster
        if abs(c - clusters[-1][-1]) <= threshold:
            clusters[-1].append(c)
        else:
            #create a new cluster
            clusters.append([c])

    # calculate mean for every cluster
    averaged = [int(sum(cluster) / len(cluster)) for cluster in clusters]
    return averaged



def detect_table_lines(edges, img):
    #image height and width
    H, W = edges.shape

    lines = cv2.HoughLinesP(
        edges,
        1,
        np.pi / 180,
        threshold=115,
        minLineLength=100,
        maxLineGap=10
    )

    vertical = []
    horizontal = []

    if lines is not None:
        for x1, y1, x2, y2 in lines[:, 0]:
            x1, y1, x2, y2 = extend_line(x1, y1, x2, y2, W, H)
            if abs(x1 - x2) < 5:
                vertical.append((x1, y1, x2, y2))
            elif abs(y1 - y2) < 5:
                horizontal.append((x1, y1, x2, y2))

    # clustering; calculate mean
    xs = cluster_lines(vertical, threshold=15)
    ys = cluster_lines(horizontal, threshold=6)

    return xs, ys

# ------------------------------------
# Extract cells
# ------------------------------------

def extract_cells(xs, ys, header_rows=1, header_cols=0, margin=3):
    """
    creates a list of all table cells based on the xs/ys

    xs, ys: sortiet lines (from detect_table_lines)
    header_rows: number of rows
    header_cols: number of colums
    margin: approximatly margin of the table lines so I can skip scanning them

    return:
      list of tuples with: (habit_idx, day_idx, x, y, w, h)

      where:
        - habit_idx = row index inside the data area (0-based)
        - day_idx = column index inside the data area (0-based)
        - x, y = top-left corner of the cell (inside margins)
        - w, h = width/height of the cell (inside margins)
    """
    rows = len(ys) - 1
    cols = len(xs) - 1

    cells = []

    for r in range(header_rows, rows):
        for c in range(header_cols, cols):
            # cell boundaries defined by the table lines
            x = xs[c]
            y = ys[r]
            w = xs[c + 1] - x #the second index (right cell boundary) minus the beginning of the cell
            h = ys[r + 1] - y

            # shrink cell area to avoid scanning on the grid lines
            cell_x = x + margin
            cell_y = y + margin
            cell_w = max(1, w - 2 * margin)
            cell_h = max(1, h - 2 * margin)

            # the area where the habits and days are written - because here are no crosses to be detected :D
            habit_idx = r - header_rows  # 0-basiert
            day_idx = c - header_cols  # 0-basiert

            cells.append((habit_idx, day_idx, cell_x, cell_y, cell_w, cell_h))

    return cells


# ------------------------------------
# Detect crosses
# ------------------------------------

def analyze_cell(gray, binary, x, y, w, h, debug=False, tag=""):
    """
    tried different approaches here to detect a cross in the cell
   	1. black pixels ratio
    2. edges ratio based on canny edge detecrtion (count white pixels)
    3. hough and detect lines
		3.1 first approach was to detect crosses with the property that the lines cross... but didnt work always
		3.2 second approach if there is a line in the cell which is slightly diagonal i assume it's a line of a cross and not a detected table line

    return:
      is_cross (bool), stats, roi_gray, edges, overlay
    """

    roi_gray = gray[y:y+h, x:x+w]
    roi_bin = binary[y:y+h, x:x+w]

    roi_blur = cv2.GaussianBlur(roi_gray, (5, 5), 0)
    edges = cv2.Canny(roi_blur, 50, 150)

    # 1. black pixels count
    black_pixels = np.count_nonzero(roi_bin > 0)
    total_pixels = roi_bin.size
    black_ratio  = black_pixels / total_pixels if total_pixels > 0 else 0.0

    # 2. edges ratio in the image based on the canny edge detection
    edge_pixels = np.count_nonzero(edges > 0)
    edge_ratio  = edge_pixels / total_pixels if total_pixels > 0 else 0.0

    # 3. - but only 3.2 - discarded 3.1
    min_len = int(0.3 * min(w, h))
    lines = cv2.HoughLinesP(
        edges,
        1,
        np.pi / 180,
        threshold=10,
        minLineLength=min_len,
        maxLineGap=2
    )

    diag_count = 0
    line_count = 0
    overlay = cv2.cvtColor(roi_gray, cv2.COLOR_GRAY2BGR)

    if lines is not None:
        for x1, y1, x2, y2 in lines[:, 0]:
            dx = x2 - x1
            dy = y2 - y1
            length = np.hypot(dx, dy)
            # TODO maybe i can ignore this because i check for straight lines and filter out possible table lines that way
            if length < min_len:
                continue

            line_count += 1

            # degree of the line (0° = horizontal, 90° = vertical)
            angle = abs(np.degrees(np.arctan2(dy, dx)))

            # remove straight lines
            # 0–15° and 165–180° could be horizontal lines
            # 75–105° could be vertical lines
            # everything else i assume as diagonal
            is_diagonal = (
                    (15 < angle < 75) or   # diagonal to north/upwards
                    (105 < angle < 165)    # diagonal to south/downwards
            )

            if is_diagonal:
                diag_count += 1
                cv2.line(overlay, (x1, y1), (x2, y2), (0, 0, 255), 2)

    stats = {
        "diag_count": diag_count,
        "line_count": line_count,
        "black_ratio": black_ratio,
        "edge_ratio": edge_ratio,
    }

    # if at least one diagonal line in that cell, there is a cross
    is_cross = diag_count > 0

    return is_cross, stats, roi_gray, edges, overlay

def detect_crosses(gray, binary, img, cells):
    """
    goes through all cells, detects crosses and shows them in the image

    cells: list of tuples from extract_cells (habit_idx, day_idx, x, y, w, h)
    return:
    	matrix[habit][day] = True/False
     	dbg_img = image with greend marked cross cells
    """
    if not cells:
        return [], img

    max_habit = max(h for h, _, _, _, _, _ in cells) + 1
    max_day = max(d for _, d, _, _, _, _ in cells) + 1

    # basically 2d array with every habits and its day; set per default to false
    matrix = [[False for _ in range(max_day)] for _ in range(max_habit)]
    dbg = img.copy()

    for habit_idx, day_idx, x, y, w, h in cells:
        is_x, stats, roi, edges, overlay = analyze_cell(gray, binary, x, y, w, h,)

        if is_x:
            matrix[habit_idx][day_idx] = True
            cv2.rectangle(
                dbg,
                (x, y),
                (x + w, y + h),
                (0, 255, 0),
                2
            )

    return matrix, dbg


def print_matrix_formatted(matrix):
    days = len(matrix[0])
    cell_w = 2
    # Header
    header = "    " + "".join(f"{d+1:>{cell_w}}" for d in range(days))
    print(header)
    # Rows
    for i, row in enumerate(matrix, 1):
        line = "".join(" X" if v else " ." for v in row)
        print(f"R{i:02d} {line}")

if __name__ == "__main__":
    max_width = 1200
    header_rows = 1
    header_cols = 0
    margin = 3


    if len(sys.argv) < 2:
        print("Usage: python image_table_detect.py <imagepath>")
        sys.exit(1)

    img_path = sys.argv[1]

    img = load_image(img_path)
    img = resize(img, max_width)
    gray, binary, edges = preprocess(img)
    xs, ys = detect_table_lines(edges, img)
    cells = extract_cells(xs, ys, header_rows=header_rows, header_cols=header_cols, margin=margin)
    matrix, _ = detect_crosses(gray, binary, img, cells)

    print(json.dumps({"matrix": matrix}))
    # print_matrix_formatted(matrix)