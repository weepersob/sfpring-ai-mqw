package com.mqw.springaimqw.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
class MyController {

    @Autowired
    private  ChatClient chatClient;   // 可以使用一些通用功能   适用与简单场景
    @Autowired
    private ChatModel chatModel;   // 可以使用不同的模型的独有功能  功能更强大
    @Autowired
    private ChatModel chatModel2;

    @GetMapping("/ai")
    String generation(@RequestParam(value = "message",defaultValue = "给我讲个笑话")String message) {
        return this.chatClient.prompt()
            .user(message)   // 设置用户输入
            .call()  // 发送请求调用模型
            .content(); // 获取模型返回string 类型的结果
    }

    @GetMapping(value = "/stream",produces = "text/md;charset=UTF-8")
    Flux<String> generationStream(@RequestParam(value = "message",defaultValue = "给我讲个笑话")String message) {
        Flux<String> output = chatClient.prompt()
                .user(message) //=new UserMessage(message)
                .system("You are a senior programmer")   // 配置类里面设置的默认角色是全局的  我们可以用这个system 来进行不同的角色设置
                .stream()
                .content();
        return output;
    }


    @GetMapping("/chatModel")
    String chatModel(@RequestParam(value = "message",defaultValue = "给我讲个笑话")String message) {
        ChatResponse response = chatModel.call(   // call改为stream即可实现流式输出
                new Prompt( // 下面一行就是用户输入的message 内部会自动生成一个UserMessage
                        "Generate the names of 5 famous pirates.",   // =new UserMessage(message)
                        OpenAiChatOptions.builder()
                                .model("deepseek-chat")
                                .temperature(0.4)
                                .build()
                ));
        return response.getResult().getOutput().getText();
    }
}