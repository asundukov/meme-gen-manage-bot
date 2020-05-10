# General way to create and manage questionnaire

1. POST `/manage/questionnaires` with `title`, `description`. Receive new `{qiestionnaireId}`
2. POST `/manage/questionnaires/{qiestionnaireId}/questions` with `value`, `points` to create new question for questionnaire. Receive new `{questionId}`
3. POST `/manage/questionnaires/{qiestionnaireId}/questions/{questionId}/options` with `value`, `isRight`, `isCustom`, `points` to create new answer option for question.
4. Retry `3.` to create more options for question `{questionId}`
5. Retry `2.` to create more questions for questionnaire `{qiestionnaireId}`
6. POST `/manage/questionnaires/{qiestionnaireId}/commit` to commit and publish questionnaire. Receive `{questionnaireUrl}`. After this manager cannot modify questionnaire, questions, options, reward points, results.

7. Send `{questionnaireUrl}` to interveieweers and await for answers at questionnaire admin panel or `resultUrl` from interveieweers
8. GET `/manage/questionnaires/{qiestionnaireId}/results` to watch results


# General way to answer questionnaire

1. POST `/public/questionnaires/{questionnaireId}/attempts` to create new attempt. Receive new `{attemptId}`
2. POST `/public/questionnaires/{questionnaireId}/attempts/{attemptId}/answers` with `{questionId}` to answer question from questionnaire.
3. Retry `2.` for each question from questionnaire.
4. POST `/public/questionnaires/{questionnaireId}/attempts/{attemptId}/commit` to send your answers to questionnaire's admin. Receive `{resultUrl}`.
