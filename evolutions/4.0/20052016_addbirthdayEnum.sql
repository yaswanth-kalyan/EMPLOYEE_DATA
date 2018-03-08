ALTER TABLE Message DROP CONSTRAINT ck_message_message_content_type; 


ALTER TABLE Message add CONSTRAINT ck_message_message_content_type CHECK (message_content_type::text = ANY (ARRAY[
 'LEFTGROUP'::character varying::text, 
 'CREATEGROUP'::character varying::text,
 'ADDTOGROUP'::character varying::text,
 'RENAMEGROUP'::character varying::text,
 'DELETEGROUP'::character varying::text,
 'COMMENT'::character varying::text,
 'TEXT'::character varying::text, 
 'FILE'::character varying::text, 
 'IMAGE'::character varying::text,
 'GITNOTIFICATION'::character varying::text,
 'SNIPPET'::character varying::text,
 'BIRTHDAY'::character varying::text,
 'LEAVESTATUS'::character varying::text,
 'URL'::character varying::text
 ]));