package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
   private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
   //用于替换敏感词的字符
    private static  final String REPLACEMENT = "***";
    //初始化根结点
    private TrieNode rootNode = new TrieNode();
    //把敏感词搞成前缀树
    @PostConstruct//初始化方法
    public void init(){
        //把敏感词通过inputStream字节流弄进来
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             //字节流转换字符流再转换成缓存流
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            //把缓冲流搞到前缀树
            String keyword;
            while((keyword = reader.readLine())!=null){
                this.addKeyword(keyword);
            }


        }catch(IOException e){
            logger.error("加载敏感词信息失败:"+e.getMessage() );
        }
    }


//把铭感词String keyword添加到前缀树
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            //获取子结点subNode是否为空，之前有没有存过
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode==null){
                //子结点为空的时候把其存进前缀树
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //进入下一层结点处理
            tempNode = subNode;
            //设置结束标识
            if(i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }
    //text为过滤前文本，返回过滤后的文本
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        //指向树的指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin =0 ;
        //指针3
        int position =0;
        //记录过滤的结果
        StringBuilder sb =new StringBuilder();

        while(position<text.length()){
            //当前字符
            char c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                //1：01不记笔记
                if (tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或者中间，指针3都向下走一步
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode==null){
                //以begin开头的字符串不是铭感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position=++begin;
                //树形结构指针指向根结点
                tempNode = rootNode;

            }
            else if (tempNode.isKeywordEnd()){
                //发现铭感词，替换
                sb.append(REPLACEMENT);
                //指针进入下一个位置
                begin= ++position;
                //树形结构指针指向根结点
                tempNode = rootNode;

            }
            else{
                position++;
            }


        }
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否为符号
    private  boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }



    //定义前缀树结构

    private class TrieNode{

        //完整敏感词结束标识
        private boolean isKeywordEnd = false;

        //树形结构子结点,key为子节点的值(字符)，value为子节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();


        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子结点,key为子节点的值(字符)，value为子节点
        private  void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        //获取子结点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

    }


}
