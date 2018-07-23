package com.taobao.csp.sentinel.dashboard.view;

import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.command.vo.NodeVo;

import com.taobao.csp.sentinel.dashboard.domain.ResourceTreeNode;
import com.taobao.csp.sentinel.dashboard.inmem.HttpHelper;
import com.taobao.csp.sentinel.dashboard.view.vo.ResourceVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author leyou
 */
@Controller
@RequestMapping(value = "/resource", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResourceController {

    private static Logger logger = LoggerFactory.getLogger(ResourceController.class);
    @Autowired
    HttpHelper httpFetcher;

    /**
     * Fetch real time statistics info of the machine.
     *
     * @param ip        ip to fetch
     * @param port      port of the ip
     * @param type      one of [root, default, cluster], 'root' means fetching from tree root node, 'default' means
     *                  fetching from tree default node, 'cluster' means fetching from cluster node.
     * @param searchKey key to search
     * @return node statistics info.
     */
    @ResponseBody
    @RequestMapping("/machineResource.json")
    Result<?> fetchIdentityOfMachine(String ip, Integer port, String type, String searchKey) {
        if (StringUtil.isEmpty(ip) || port == null) {
            return Result.ofFail(-1, "invalid param, give ip, port");
        }
        final String ROOT = "root";
        final String DEFAULT = "default";
        if (StringUtil.isEmpty(type)) {
            type = ROOT;
        }
        if (ROOT.equalsIgnoreCase(type) || DEFAULT.equalsIgnoreCase(type)) {
            List<NodeVo> nodeVos = httpFetcher.fetchResourceOfMachine(ip, port, type);
            if (nodeVos == null) {
                return Result.ofSuccess(null);
            }
            ResourceTreeNode treeNode = ResourceTreeNode.fromNodeVoList(nodeVos);
            treeNode.searchIgnoreCase(searchKey);
            return Result.ofSuccess(ResourceVo.fromResourceTreeNode(treeNode));
        } else {// cluster
            List<NodeVo> nodeVos = httpFetcher.fetchClusterNodeOfMachine(ip, port, true);
            if (nodeVos == null) {
                return Result.ofSuccess(null);
            }
            if (StringUtil.isNotEmpty(searchKey)) {
                nodeVos = nodeVos.stream().filter(node -> node.getResource()
                    .toLowerCase().contains(searchKey.toLowerCase()))
                    .collect(Collectors.toList());
            }
            return Result.ofSuccess(ResourceVo.fromNodeVoList(nodeVos));
        }
    }
}
