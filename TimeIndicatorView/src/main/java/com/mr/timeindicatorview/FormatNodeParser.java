package com.mr.timeindicatorview;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/5/16
 * @description:
 */
public class FormatNodeParser {

    public String dateFormat;

    private List<String> nodeList = Arrays.asList(TimeIndicatorView.YEARS, TimeIndicatorView.MONTH
            , TimeIndicatorView.DAY, TimeIndicatorView.HOURS, TimeIndicatorView.MINUTE
            , TimeIndicatorView.SECONDS, TimeIndicatorView.MILLISECOND);

    private List<String> suffixList = Arrays.asList(TimeIndicatorView.SUFFIX1
            , TimeIndicatorView.SUFFIX2, TimeIndicatorView.SUFFIX3, TimeIndicatorView.SUFFIX4
            , TimeIndicatorView.SUFFIX5, TimeIndicatorView.SUFFIX6, TimeIndicatorView.SUFFIX7
            , TimeIndicatorView.SUFFIX8, TimeIndicatorView.SUFFIX9, TimeIndicatorView.SUFFIX10
            , TimeIndicatorView.SUFFIX11);

    private List<FormatNode> formatNodes = new ArrayList<>();

    public FormatNodeParser(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void parsing() {
        if (!TextUtils.isEmpty(dateFormat)) {
            for (String nodeStr : nodeList) {
                if (dateFormat.contains(nodeStr)) {
                    FormatNode node = new FormatNode();
                    node.format = nodeStr;
                    node.isPointer = true;
                    formatNodes.add(node);
                }
            }
            for (String suffixStr : suffixList) {
                if (dateFormat.contains(suffixStr)) {
                    FormatNode node = new FormatNode();
                    node.format = suffixStr;
                    node.isPointer = false;
                    formatNodes.add(node);
                }
            }


            //最后处理排序,减少循环次数
            List<FormatNode> nodes = new ArrayList<>();
            nodes.addAll(formatNodes);
            formatNodes.clear();
            String dateFormatStr = dateFormat;

            int whileCount = 0;//防止出现意外死循环

            while (!TextUtils.isEmpty(dateFormatStr)
                    && whileCount < nodes.size()) {
                whileCount++;
                for (FormatNode node : nodes) {
                    if (dateFormatStr.startsWith(node.format)) {
                        dateFormatStr = dateFormatStr.substring(node.format.length());
                        formatNodes.add(node);
                        continue;
                    }
                }
            }
        }
    }

    public List<FormatNode> getFormatNodes() {
        return formatNodes;
    }

}
