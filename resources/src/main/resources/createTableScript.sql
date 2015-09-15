CREATE TABLE project
(
  id bigint NOT NULL,
  createdat character varying(255),
  developers integer NOT NULL,
  htmlurl character varying(255),
  message character varying(255),
  name character varying(255),
  numberconflictingmerges integer NOT NULL,
  numbermergerevisions integer NOT NULL,
  numberrevisions integer NOT NULL,
  priva boolean NOT NULL,
  repositorypath character varying(255),
  searchurl character varying(255),
  updatedat character varying(255),
  CONSTRAINT project_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE project
  OWNER TO postgres;




CREATE TABLE language
(
  id BIGSERIAL NOT NULL ,
  name character varying(255),
  percentage double precision NOT NULL,
  size double precision NOT NULL,
  project_id bigint REFERENCES project(id),
  CONSTRAINT language_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE language
  OWNER TO postgres;



CREATE TABLE revision
(
  id BIGSERIAL NOT NULL,
  basesha character varying(255),
  leftsha character varying(255),
  numberconflictingfiles integer NOT NULL,
  numberjavaconflictingfiles integer NOT NULL,
  rightsha character varying(255),
  sha character varying(255),    
  status character varying(255), 
  project_id bigint REFERENCES project(id),
  CONSTRAINT revision_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE revision
  OWNER TO postgres;



CREATE TABLE conflictingfile
(
  id BIGSERIAL NOT NULL,
  filetype character varying(255),
  name character varying(255),
  path character varying(255),
  removed boolean NOT NULL,
  revision_id bigint REFERENCES revision(id),
  CONSTRAINT conflictingfile_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE conflictingfile
  OWNER TO postgres;



CREATE TABLE conflictingchunk
(
  id BIGSERIAL NOT NULL,
  beginline integer NOT NULL,
  developerdecision character varying(255),
  endline integer NOT NULL,
  identifier character varying(255),
  separatorline integer NOT NULL,
  conflictingFile_id bigint REFERENCES conflictingfile(id),
  CONSTRAINT conflictingchunk_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE conflictingchunk
  OWNER TO postgres;



CREATE TABLE kindconflict
(
  id BIGSERIAL NOT NULL,
  beginline integer NOT NULL,
  endline integer NOT NULL,
  side character varying(255), 
  conflictingChunk_id bigint REFERENCES conflictingchunk(id),
  CONSTRAINT kindconflict_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE kindconflict
  OWNER TO postgres;



CREATE TABLE languageconstruct
(
  id BIGSERIAL NOT NULL,
  begincolumn integer NOT NULL,
  begincolumnblock integer NOT NULL,
  beginline integer NOT NULL,
  beginlineblock integer NOT NULL,
  endcolumn integer NOT NULL,
  endcolumnblock integer NOT NULL,
  endline integer NOT NULL,
  endlineblock integer NOT NULL,
  hasblock boolean NOT NULL,
  name character varying(255),
  kindconflict_id bigint REFERENCES kindconflict(id),
  CONSTRAINT languageconstruct_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE languageconstruct
  OWNER TO postgres;



CREATE TABLE solutioncontent
(
  id  BIGSERIAL NOT NULL,
  content text,
  conflictingChunk_id bigint REFERENCES conflictingchunk(id),
  CONSTRAINT solutioncontent_pkey PRIMARY KEY (id)

)
WITH (
  OIDS=FALSE
);
ALTER TABLE solutioncontent
  OWNER TO postgres;



CREATE TABLE conflictingcontent
(
  id  BIGSERIAL NOT NULL,
  content text,
  conflictingChunk_id bigint REFERENCES conflictingchunk(id),
  CONSTRAINT conflictingcontent_pkey PRIMARY KEY (id)

)
WITH (
  OIDS=FALSE
);
ALTER TABLE conflictingcontent
  OWNER TO postgres;