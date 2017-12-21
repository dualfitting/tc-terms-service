SELECT 	tou.terms_of_use_id as termsOfUseId,
	tou.title as title, 
	tou.url as url,
    tou.terms_text as text,
    touat.terms_of_use_agreeability_type_id as agreeabilityTypeId,
    toudtx.docusign_template_id as docusignTemplateId 
FROM common_oltp\:terms_of_use tou
INNER JOIN common_oltp\:terms_of_use_agreeability_type_lu touat ON touat.terms_of_use_agreeability_type_id = tou.terms_of_use_agreeability_type_id
LEFT JOIN common_oltp\:terms_of_use_docusign_template_xref toudtx ON toudtx.terms_of_use_id = tou.terms_of_use_id
WHERE tou.terms_of_use_id = :termsOfUseId