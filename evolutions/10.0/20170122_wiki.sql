CREATE SEQUENCE public.page_id_seq
    INCREMENT 1
    START 3
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.page_id_seq
    OWNER TO postgres;
    
CREATE SEQUENCE public.page_history_id_seq
    INCREMENT 1
    START 4
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.page_history_id_seq
    OWNER TO postgres;


-- Table: public.page

-- DROP TABLE public.page;

CREATE TABLE public.page
(
    id bigint NOT NULL DEFAULT nextval('page_id_seq'::regclass),
    title character varying(255) COLLATE pg_catalog."default" NOT NULL,
    is_active boolean,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL,
    CONSTRAINT pk_page PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.page
    OWNER to postgres;
    
    
    -- Table: public.page_history

-- DROP TABLE public.page_history;

CREATE TABLE public.page_history
(
    id bigint NOT NULL DEFAULT nextval('page_history_id_seq'::regclass),
    version integer,
    content text COLLATE pg_catalog."default",
    page_id bigint,
    app_user_id bigint,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL,
    CONSTRAINT pk_page_history PRIMARY KEY (id),
    CONSTRAINT fk_page_history_appuser_56 FOREIGN KEY (app_user_id)
        REFERENCES public.app_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_page_history_page_55 FOREIGN KEY (page_id)
        REFERENCES public.page (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.page_history
    OWNER to postgres;

-- Index: ix_page_history_appuser_56

-- DROP INDEX public.ix_page_history_appuser_56;

CREATE INDEX ix_page_history_appuser_56
    ON public.page_history USING btree
    (app_user_id)
    TABLESPACE pg_default;

-- Index: ix_page_history_page_55

-- DROP INDEX public.ix_page_history_page_55;

CREATE INDEX ix_page_history_page_55
    ON public.page_history USING btree
    (page_id)
    TABLESPACE pg_default;