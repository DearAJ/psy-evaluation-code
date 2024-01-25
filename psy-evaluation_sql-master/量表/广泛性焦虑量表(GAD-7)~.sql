--  量表
INSERT INTO `psy_evaluation`.`scale` (`name`, `level`, `create_user`, `create_time`, `update_time`, `deleted`, `duration`, `introduction`, `conclusion`, `type`, `application`, `warning_options`, `crowd`) VALUES ('广泛性焦虑量表(GAD-7)', '2', '0', '2022-12-19 13:13:00', '2022-12-19 13:13:00', '0', '35', '在过去的两周里，你生活中有多少天出现以下的症状？请在答案对应的位置选择。######广泛性焦虑障碍量表 (Generalized Anxiexy Disorde-7, GAD-7)是Spizer R，K roenke K 等人于2006年编制而成。广泛用于临床评估焦虑情绪，定期（1次/2周）自评可以观察焦虑情绪变化趋势和治疗效果。\nGAD-7对不同人群的广泛性焦虑筛查具有较好的信度和效度，可作为快速、简便、可靠、有效的筛查工具。', '在过去的两周里，你生活中有多少天出现以下的症状？请在答案对应的位置打“√”', '1', '心理健康', '0', '高中以上、教师');

--  模块
INSERT INTO `psy_evaluation`.`module` (`id`, `scale_id`, `name`, `mean`, `create_time`, `update_time`, `deleted`, `score_threshold`) VALUES ((select MAX(id)+1 from module m),(SELECT id from scale where `name`='广泛性焦虑量表(GAD-7)'), '总分', '0:4/正常水平;5:9/轻度焦虑;10:14/中度焦虑;15:21/重度焦虑', '2022-12-19 14:25:30', '2022-12-19 14:25:34', '0', '15');

--  题目
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='广泛性焦虑量表(GAD-7)'), '感觉紧张，焦虑或急切', '1', (select MAX(id) from module m), NULL, NULL, NULL, NULL, '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不会', '0', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '好几天', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '超过一周', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '几乎每天', '3', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='广泛性焦虑量表(GAD-7)'), '不能够停止或控制担忧', '1', (select MAX(id) from module m), NULL, NULL, NULL, NULL, '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不会', '0', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '好几天', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '超过一周', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '几乎每天', '3', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='广泛性焦虑量表(GAD-7)'), '对各种各样的事情担忧过多', '1', (select MAX(id) from module m), NULL, NULL, NULL, NULL, '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不会', '0', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '好几天', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '超过一周', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '几乎每天', '3', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='广泛性焦虑量表(GAD-7)'), '很难放松下来', '1', (select MAX(id) from module m), NULL, NULL, NULL, NULL, '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不会', '0', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '好几天', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '超过一周', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '几乎每天', '3', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='广泛性焦虑量表(GAD-7)'), '由于不安而无法静坐', '1', (select MAX(id) from module m), NULL, NULL, NULL, NULL, '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不会', '0', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '好几天', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '超过一周', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '几乎每天', '3', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='广泛性焦虑量表(GAD-7)'), '变得容易烦躁或急躁', '1', (select MAX(id) from module m), NULL, NULL, NULL, NULL, '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不会', '0', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '好几天', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '超过一周', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '几乎每天', '3', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='广泛性焦虑量表(GAD-7)'), '感到似乎将有可怕的事情发生而害怕', '1', (select MAX(id) from module m), NULL, NULL, NULL, NULL, '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不会', '0', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '好几天', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '超过一周', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '几乎每天', '3', '0');
