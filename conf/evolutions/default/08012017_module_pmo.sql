

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 195 (class 1259 OID 72623)
-- Name: bug; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE bug (
    id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL
);


ALTER TABLE bug OWNER TO postgres;

--
-- TOC entry 194 (class 1259 OID 72621)
-- Name: bug_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE bug_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE bug_id_seq OWNER TO postgres;

--
-- TOC entry 2683 (class 0 OID 0)
-- Dependencies: 194
-- Name: bug_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE bug_id_seq OWNED BY bug.id;


--
-- TOC entry 223 (class 1259 OID 72762)
-- Name: epic; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE epic (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255),
    road_map_id bigint,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL
);


ALTER TABLE epic OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 72760)
-- Name: epic_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE epic_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE epic_id_seq OWNER TO postgres;

--
-- TOC entry 2684 (class 0 OID 0)
-- Dependencies: 222
-- Name: epic_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE epic_id_seq OWNED BY epic.id;


--
-- TOC entry 263 (class 1259 OID 72978)
-- Name: ptask; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ptask (
    id bigint NOT NULL,
    name character varying(255),
    description text,
    estimated_time numeric(10,2),
    actual_time numeric(10,2),
    planned_start_date timestamp without time zone,
    planned_end_date timestamp without time zone,
    actual_start_date timestamp without time zone,
    actual_end_date timestamp without time zone,
    user_story_id bigint,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL
);


ALTER TABLE ptask OWNER TO postgres;

--
-- TOC entry 262 (class 1259 OID 72976)
-- Name: ptask_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ptask_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ptask_id_seq OWNER TO postgres;

--
-- TOC entry 2685 (class 0 OID 0)
-- Dependencies: 262
-- Name: ptask_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ptask_id_seq OWNED BY ptask.id;


--
-- TOC entry 305 (class 1259 OID 73225)
-- Name: road_map; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE road_map (
    id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    project_id bigint NOT NULL,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL
);


ALTER TABLE road_map OWNER TO postgres;

--
-- TOC entry 304 (class 1259 OID 73223)
-- Name: road_map_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE road_map_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE road_map_id_seq OWNER TO postgres;

--
-- TOC entry 2686 (class 0 OID 0)
-- Dependencies: 304
-- Name: road_map_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE road_map_id_seq OWNED BY road_map.id;


--
-- TOC entry 309 (class 1259 OID 73246)
-- Name: sprint; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sprint (
    id bigint NOT NULL,
    name character varying(255),
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL
);


ALTER TABLE sprint OWNER TO postgres;

--
-- TOC entry 308 (class 1259 OID 73244)
-- Name: sprint_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE sprint_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sprint_id_seq OWNER TO postgres;

--
-- TOC entry 2687 (class 0 OID 0)
-- Dependencies: 308
-- Name: sprint_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE sprint_id_seq OWNED BY sprint.id;


--
-- TOC entry 321 (class 1259 OID 73311)
-- Name: test_case; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE test_case (
    id bigint NOT NULL,
    name character varying(255),
    pre_conditions character varying(255),
    descriptions character varying(255),
    steps character varying(255),
    sample_input character varying(255),
    expected_output character varying(255),
    test_scenario_id bigint,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL
);


ALTER TABLE test_case OWNER TO postgres;

--
-- TOC entry 320 (class 1259 OID 73309)
-- Name: test_case_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE test_case_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE test_case_id_seq OWNER TO postgres;

--
-- TOC entry 2688 (class 0 OID 0)
-- Dependencies: 320
-- Name: test_case_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE test_case_id_seq OWNED BY test_case.id;


--
-- TOC entry 323 (class 1259 OID 73322)
-- Name: test_execution; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE test_execution (
    id bigint NOT NULL,
    test_case_id bigint,
    test_result character varying(2),
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL,
    CONSTRAINT ck_test_execution_test_result CHECK (((test_result)::text = ANY ((ARRAY['O'::character varying, 'P'::character varying, 'NE'::character varying, 'B'::character varying, 'F'::character varying])::text[])))
);


ALTER TABLE test_execution OWNER TO postgres;

--
-- TOC entry 322 (class 1259 OID 73320)
-- Name: test_execution_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE test_execution_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE test_execution_id_seq OWNER TO postgres;

--
-- TOC entry 2689 (class 0 OID 0)
-- Dependencies: 322
-- Name: test_execution_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE test_execution_id_seq OWNED BY test_execution.id;


--
-- TOC entry 325 (class 1259 OID 73333)
-- Name: test_run; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE test_run (
    id bigint NOT NULL,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL
);


ALTER TABLE test_run OWNER TO postgres;

--
-- TOC entry 324 (class 1259 OID 73331)
-- Name: test_run_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE test_run_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE test_run_id_seq OWNER TO postgres;

--
-- TOC entry 2690 (class 0 OID 0)
-- Dependencies: 324
-- Name: test_run_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE test_run_id_seq OWNED BY test_run.id;


--
-- TOC entry 327 (class 1259 OID 73341)
-- Name: test_scenario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE test_scenario (
    id bigint NOT NULL,
    user_story_id bigint,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL
);


ALTER TABLE test_scenario OWNER TO postgres;

--
-- TOC entry 326 (class 1259 OID 73339)
-- Name: test_scenario_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE test_scenario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE test_scenario_id_seq OWNER TO postgres;

--
-- TOC entry 2691 (class 0 OID 0)
-- Dependencies: 326
-- Name: test_scenario_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE test_scenario_id_seq OWNED BY test_scenario.id;


--
-- TOC entry 339 (class 1259 OID 73403)
-- Name: user_story; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE user_story (
    id bigint NOT NULL,
    road_map_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255),
    epic_id bigint NOT NULL,
    sprint_id bigint,
    created_on timestamp without time zone NOT NULL,
    last_update timestamp without time zone NOT NULL
);


ALTER TABLE user_story OWNER TO postgres;

--
-- TOC entry 338 (class 1259 OID 73401)
-- Name: user_story_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE user_story_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_story_id_seq OWNER TO postgres;

--
-- TOC entry 2692 (class 0 OID 0)
-- Dependencies: 338
-- Name: user_story_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE user_story_id_seq OWNED BY user_story.id;


--
-- TOC entry 2514 (class 2604 OID 72626)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY bug ALTER COLUMN id SET DEFAULT nextval('bug_id_seq'::regclass);


--
-- TOC entry 2515 (class 2604 OID 72765)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epic ALTER COLUMN id SET DEFAULT nextval('epic_id_seq'::regclass);


--
-- TOC entry 2516 (class 2604 OID 72981)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ptask ALTER COLUMN id SET DEFAULT nextval('ptask_id_seq'::regclass);


--
-- TOC entry 2517 (class 2604 OID 73228)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY road_map ALTER COLUMN id SET DEFAULT nextval('road_map_id_seq'::regclass);


--
-- TOC entry 2518 (class 2604 OID 73249)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sprint ALTER COLUMN id SET DEFAULT nextval('sprint_id_seq'::regclass);


--
-- TOC entry 2519 (class 2604 OID 73314)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_case ALTER COLUMN id SET DEFAULT nextval('test_case_id_seq'::regclass);


--
-- TOC entry 2520 (class 2604 OID 73325)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_execution ALTER COLUMN id SET DEFAULT nextval('test_execution_id_seq'::regclass);


--
-- TOC entry 2522 (class 2604 OID 73336)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_run ALTER COLUMN id SET DEFAULT nextval('test_run_id_seq'::regclass);


--
-- TOC entry 2523 (class 2604 OID 73344)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_scenario ALTER COLUMN id SET DEFAULT nextval('test_scenario_id_seq'::regclass);


--
-- TOC entry 2524 (class 2604 OID 73406)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_story ALTER COLUMN id SET DEFAULT nextval('user_story_id_seq'::regclass);


--
-- TOC entry 2526 (class 2606 OID 72631)
-- Name: pk_bug; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY bug
    ADD CONSTRAINT pk_bug PRIMARY KEY (id);


--
-- TOC entry 2529 (class 2606 OID 72770)
-- Name: pk_epic; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epic
    ADD CONSTRAINT pk_epic PRIMARY KEY (id);


--
-- TOC entry 2532 (class 2606 OID 72986)
-- Name: pk_ptask; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ptask
    ADD CONSTRAINT pk_ptask PRIMARY KEY (id);


--
-- TOC entry 2535 (class 2606 OID 73233)
-- Name: pk_road_map; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY road_map
    ADD CONSTRAINT pk_road_map PRIMARY KEY (id);


--
-- TOC entry 2537 (class 2606 OID 73251)
-- Name: pk_sprint; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sprint
    ADD CONSTRAINT pk_sprint PRIMARY KEY (id);


--
-- TOC entry 2540 (class 2606 OID 73319)
-- Name: pk_test_case; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_case
    ADD CONSTRAINT pk_test_case PRIMARY KEY (id);


--
-- TOC entry 2543 (class 2606 OID 73328)
-- Name: pk_test_execution; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_execution
    ADD CONSTRAINT pk_test_execution PRIMARY KEY (id);


--
-- TOC entry 2547 (class 2606 OID 73338)
-- Name: pk_test_run; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_run
    ADD CONSTRAINT pk_test_run PRIMARY KEY (id);


--
-- TOC entry 2550 (class 2606 OID 73346)
-- Name: pk_test_scenario; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_scenario
    ADD CONSTRAINT pk_test_scenario PRIMARY KEY (id);


--
-- TOC entry 2555 (class 2606 OID 73411)
-- Name: pk_user_story; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_story
    ADD CONSTRAINT pk_user_story PRIMARY KEY (id);


--
-- TOC entry 2545 (class 2606 OID 73330)
-- Name: uq_test_execution_test_case_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_execution
    ADD CONSTRAINT uq_test_execution_test_case_id UNIQUE (test_case_id);


--
-- TOC entry 2527 (class 1259 OID 73620)
-- Name: ix_epic_roadmap_21; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_epic_roadmap_21 ON epic USING btree (road_map_id);


--
-- TOC entry 2530 (class 1259 OID 73818)
-- Name: ix_ptask_userstory_54; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_ptask_userstory_54 ON ptask USING btree (user_story_id);


--
-- TOC entry 2533 (class 1259 OID 73992)
-- Name: ix_road_map_project_83; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_road_map_project_83 ON road_map USING btree (project_id);


--
-- TOC entry 2538 (class 1259 OID 74058)
-- Name: ix_test_case_testscenario_94; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_test_case_testscenario_94 ON test_case USING btree (test_scenario_id);


--
-- TOC entry 2541 (class 1259 OID 74064)
-- Name: ix_test_execution_testcase_95; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_test_execution_testcase_95 ON test_execution USING btree (test_case_id);


--
-- TOC entry 2548 (class 1259 OID 74070)
-- Name: ix_test_scenario_userstory_96; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_test_scenario_userstory_96 ON test_scenario USING btree (user_story_id);


--
-- TOC entry 2551 (class 1259 OID 74130)
-- Name: ix_user_story_epic_106; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_user_story_epic_106 ON user_story USING btree (epic_id);


--
-- TOC entry 2552 (class 1259 OID 74124)
-- Name: ix_user_story_road_map_105; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_user_story_road_map_105 ON user_story USING btree (road_map_id);


--
-- TOC entry 2553 (class 1259 OID 74136)
-- Name: ix_user_story_sprint_107; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ix_user_story_sprint_107 ON user_story USING btree (sprint_id);


--
-- TOC entry 2556 (class 2606 OID 73615)
-- Name: fk_epic_roadmap_21; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epic
    ADD CONSTRAINT fk_epic_roadmap_21 FOREIGN KEY (road_map_id) REFERENCES road_map(id);


--
-- TOC entry 2557 (class 2606 OID 73813)
-- Name: fk_ptask_userstory_54; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ptask
    ADD CONSTRAINT fk_ptask_userstory_54 FOREIGN KEY (user_story_id) REFERENCES user_story(id);


--
-- TOC entry 2558 (class 2606 OID 73987)
-- Name: fk_road_map_project_83; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY road_map
    ADD CONSTRAINT fk_road_map_project_83 FOREIGN KEY (project_id) REFERENCES projects(id);


--
-- TOC entry 2559 (class 2606 OID 74053)
-- Name: fk_test_case_testscenario_94; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_case
    ADD CONSTRAINT fk_test_case_testscenario_94 FOREIGN KEY (test_scenario_id) REFERENCES test_scenario(id);


--
-- TOC entry 2560 (class 2606 OID 74059)
-- Name: fk_test_execution_testcase_95; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_execution
    ADD CONSTRAINT fk_test_execution_testcase_95 FOREIGN KEY (test_case_id) REFERENCES test_case(id);


--
-- TOC entry 2561 (class 2606 OID 74065)
-- Name: fk_test_scenario_userstory_96; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_scenario
    ADD CONSTRAINT fk_test_scenario_userstory_96 FOREIGN KEY (user_story_id) REFERENCES user_story(id);


--
-- TOC entry 2563 (class 2606 OID 74125)
-- Name: fk_user_story_epic_106; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_story
    ADD CONSTRAINT fk_user_story_epic_106 FOREIGN KEY (epic_id) REFERENCES epic(id);


--
-- TOC entry 2562 (class 2606 OID 74119)
-- Name: fk_user_story_road_map_105; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_story
    ADD CONSTRAINT fk_user_story_road_map_105 FOREIGN KEY (road_map_id) REFERENCES road_map(id);


--
-- TOC entry 2564 (class 2606 OID 74131)
-- Name: fk_user_story_sprint_107; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_story
    ADD CONSTRAINT fk_user_story_sprint_107 FOREIGN KEY (sprint_id) REFERENCES sprint(id);


-- Completed on 2017-01-09 14:39:20 IST

--
-- PostgreSQL database dump complete
--

