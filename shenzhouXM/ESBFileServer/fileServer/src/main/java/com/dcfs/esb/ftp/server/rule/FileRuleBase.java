package com.dcfs.esb.ftp.server.rule;

import java.util.ArrayList;


public class FileRuleBase implements IFileRule {
    String filePath = null;
    ArrayList<IFileRule> ruleList = new ArrayList<>();

    public FileRuleBase(String filePath) {
        this.filePath = filePath;
    }

    public void doRule(String fileName) {
        for (IFileRule rule : ruleList) {
            rule.doRule(fileName);
        }
    }

    public boolean checkRule(String fileName) {
        return fileName != null && fileName.startsWith(filePath);
    }

    public void addRule(IFileRule rule) {
        this.ruleList.add(rule);
    }
}
