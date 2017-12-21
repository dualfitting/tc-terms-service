SELECT 
docusign_envelope_id AS docusignEnvelopeId,
docusign_template_id AS docusignTemplateId,
user_id AS userId,
is_completed AS isCompleted  
FROM informixoltp\:docusign_envelope 
WHERE user_id = :userId
AND docusign_template_id = :templateId
AND is_completed = 1 