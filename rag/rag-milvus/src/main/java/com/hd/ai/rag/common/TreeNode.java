package com.hd.ai.rag.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNode {
    private String id;
    private String title;
    private String type;
    private String path;
    private List<TreeNode> children=new ArrayList<TreeNode>();
}
