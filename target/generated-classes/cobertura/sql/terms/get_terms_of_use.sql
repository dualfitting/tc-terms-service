SELECT 	tou.terms_of_use_id as termsOfUseId,
	tou.title,
	tou.url,
	touat.name as agreeabilityType,
	tou.terms_text as text,
    touat.terms_of_use_agreeability_type_id as agreeabilityTypeId,
    toudtx.docusign_template_id as docusignTemplateId,
    (utuox.user_id IS NOT NULL) as agreed
FROM common_oltp\:terms_of_use tou
INNER JOIN common_oltp\:terms_of_use_agreeability_type_lu touat ON touat.terms_of_use_agreeability_type_id = tou.terms_of_use_agreeability_type_id
LEFT JOIN common_oltp\:terms_of_use_docusign_template_xref toudtx ON toudtx.terms_of_use_id = tou.terms_of_use_id
LEFT JOIN common_oltp\:user_terms_of_use_xref utuox ON utuox.terms_of_use_id = tou.terms_of_use_id AND utuox.user_id = :userId
WHERE tou.terms_of_use_id = :termsOfUseId