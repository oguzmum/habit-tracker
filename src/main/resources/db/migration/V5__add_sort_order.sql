ALTER TABLE public.habits ADD COLUMN sort_order INTEGER;

UPDATE public.habits h
SET sort_order = ordered.rn
FROM (
    SELECT id, ROW_NUMBER() OVER (ORDER BY priority NULLS LAST, id) AS rn
    FROM public.habits
) AS ordered
WHERE h.id = ordered.id;

ALTER TABLE public.habits ALTER COLUMN sort_order SET NOT NULL;
ALTER TABLE public.habits ALTER COLUMN sort_order SET DEFAULT 0;
