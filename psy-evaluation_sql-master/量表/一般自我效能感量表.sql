--  量表
INSERT INTO `psy_evaluation`.`scale` ( `name`, `level`, `create_user`, `create_time`, `update_time`, `deleted`, `duration`, `introduction`, `conclusion`, `type`, `application`, `warning_options`, `crowd`) VALUES (  '一般自我效能感量表', '2', '0', '2023-02-02 13:36:12', '2023-02-02 13:36:12', '0', 25, '以下10个句子关于你平时对你自己的一般看法，请你根据你的实际情况（实际感受）点击合适的选项，答案没有对错之分，请根据实际情况，快速作答，必须答齐全部10道题目，否则无效。######自我效能感是指个体对自己面对环境中的挑战能否采取适应性的行为的知觉或信念。一个相信自己能处理好各种事情的人，在生活中会更积极、更主动。这种“能做什么”的认知反映了一种个体对环境的控制感。因此自我效能感是以自信的理论看待个体处理生活中各种压力的能力。\n一般自我效能感量表（General Self-Efficacy Scale，GSES），最早由德国柏林自由大学的著名临床和健康心理学家Ralf Schwarzer 教授和他的同事于1981年编制完成，中文版的GSES最早由张建新和Schwarzer修改使用。至今中文版GSES已被证明具有良好的信度和效度。一般来说，该量表专用于大、中学生群体。', '以下10个句子关于你平时对你自己的一般看法，请你根据你的实际情况（实际感受）点击合适的选项，答案没有对错之分，请根据实际情况，快速作答，必须答齐全部10道题目，否则无效。', '1', '心理健康', '0', '大、中学生群体');

--  模块
INSERT INTO `psy_evaluation`.`module` (`id`, `scale_id`, `name`, `mean`, `create_time`, `update_time`, `deleted`, `score_threshold`) VALUES ((select MAX(id)+1 from module m),(SELECT id from scale where `name`='一般自我效能感量表'), '总分', '1:10/你的自信心很低，甚至有点自卑，在压力面前束手无策，易受影响。建议经常鼓励自己，相信自己是行的，正确对待自己的优点和缺点，学会欣赏自己。;11:20/你的自信心偏低，有时会感到信心不足，畏缩不前。找出自己的优点，承认它们，欣赏自己。;12:30/你的自信心较高，内心强大。能正确的看待自己的优缺点，遇事理智处理，乐于迎接挑战。;31:40/你的自信心非常高，极其认可自己的能力，但要注意正确看待自己的缺点。;', '2023-02-03 13:30:32', '2023-02-03 13:30:36', '0', 5);


--  题目
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '如果我尽力去做的话，我总是能够解决问题的。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '即使别人反对我，我仍有办法取得我所要的。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '对我来说，坚持理想和达成目标是轻而易举的。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '我自信能有效地应付任何突如其来的事情。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '以我的才智，我定能应付意料之外的情况。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '如果我付出必要的努力，我一定能解决大多数的难题。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '我能冷静地面对困难，因为我可信赖自己处理问题的能力。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '面对一个难题时，我通常能找到几个解决方法。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '有麻烦的时候，我通常能想到一些应付的方法。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
INSERT INTO `psy_evaluation`.`questions` (`scale_id`, `main`, `type`, `module_id`, `required`, `min`, `max`, `gear`, `deleted`) VALUES ((SELECT id from scale where `name`='一般自我效能感量表'), '无论什么事在我身上发生，我都能够应付自如。', '1', (SELECT id from module where `name`='总分' and scale_id=(SELECT id from scale where `name`='一般自我效能感量表')), NULL, NULL, NULL, NULL, '0'); 
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全不正确', '1', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '有点正确', '2', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '多数正确', '3', '0');
INSERT INTO `psy_evaluation`.`options` (`question_id`, `main`, `score`, `deleted`) VALUES ((select MAX(id) from questions q), '完全正确', '4', '0');
