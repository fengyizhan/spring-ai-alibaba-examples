package com.hd.ai.rag.controller;

import com.hd.ai.rag.common.TreeNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/coding/agent/workspace")
public class CodingAgentController {

    @GetMapping("/tree")
    public ResponseEntity<List<TreeNode>> getDirectoryTree(
            @RequestParam(defaultValue = "") String path) {
        try {
            File root = path.isEmpty() ?
                    new File(System.getProperty("user.dir")) :
                    new File(path);
            return ResponseEntity.ok(buildTree(root));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    private List<TreeNode> buildTree(File file) {
        List<TreeNode> nodes = new ArrayList<>();
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    TreeNode node = new TreeNode();
                    node.setId(child.getAbsolutePath());
                    node.setName(child.getName());
                    node.setType(child.isDirectory() ? "folder" : "file");
                    node.setPath(child.getAbsolutePath());
                    if (child.isDirectory()) {
                        node.setChildren(buildTree(child));
                    }
                    nodes.add(node);
                }
            }
        }
        return nodes;
    }
}

