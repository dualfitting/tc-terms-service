SELECT 
docusign_envelope_id AS docusignEnvelopeId,
docusign_template_id AS docusignTemplateId,
user_id AS userId,
is_completed AS isCompleted  
FROM informixoltp\:docusign_envelope
WHERE LOWER(docusign_envelope_id) = :envelopeId OR UPPER(docusign_envelope_id) = :envelopeId;