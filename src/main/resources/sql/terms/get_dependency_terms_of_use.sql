SELECT utoux.user_id
FROM common_oltp\:terms_of_use_dependency toud,
    OUTER common_oltp\:user_terms_of_use_xref utoux
WHERE toud.dependency_terms_of_use_id = utoux.terms_of_use_id
AND toud.dependent_terms_of_use_id = :termsOfUseId
AND utoux.user_id = :userId