$(function ()
{
    let codeMap=new Map();
    window.selectSourceCode=codeMap;
    //const data = JSON.parse(sessionStorage.getItem('documentsData'));
    // 回车发送
    //$('#messageInput').keypress(e => e.which === 13 && sendMessage());
    $('.selectpicker').selectpicker();

    // 获取选中值示例
    $('#selectDemandDocs').on('changed.bs.select', function (e) {
        console.log($(this).val());
    });
    // 获取选中值示例
    $('#selectDesignDocs').on('changed.bs.select', function (e) {
        console.log($(this).val());
    });
    var currentPage=1;
    var pageSize=20;
    //加载设计文档下拉列表
    window.selectCode=[];
    $.ajax({
        method: 'POST',
        data:{pageNo: currentPage, pageSize: pageSize },
        url: `/api/design-docs/list`,
        success: function(data) {
            var records=data.list;
            for(var i in records)
            {
                var r=records[i];
                $('#selectDesignDocs').append("<option value='"+r.id+"'>"+r.text+"</option>");
            }
            $('#selectDesignDocs').selectpicker('refresh');
        }
    });
    //加载需求文档下拉列表
    $.ajax({
        method: 'POST',
        url: '/api/demand-docs/list',
        dataType: 'json',
        data: {pageNo: currentPage, pageSize: pageSize },
        success: function(data) {
            var records=data.list;
            for(var i in records)
            {
                var r=records[i];
                $('#selectDemandDocs').append("<option value='"+r.id+"'>"+r.text+"</option>");
            }
            $('#selectDemandDocs').selectpicker('refresh');
        }
    });

    //加载左侧代码菜单树
    layui.use(['tree'], function(){
        var tree = layui.tree;
        // 渲染树形结构
        $.get('/coding/agent/fileTree', function(res)
        {
            var fileData=res;
            tree.render({
                elem: '#codeTree',
                data: fileData,
                showCheckbox: true,
                id: 'demoTree',
                // 自定义节点模板
                template: function(item) {
                    // 返回HTML字符串
                    return `<span class="${item.type === 'file' ? 'leaf-node-bold' : 'folder-node'}">
        <i class="layui-icon ${
                        item.type === 'folder' ? 'layui-icon-layer' : 'layui-icon-file'
                    }"></i>
        ${item.title}
      </span>`;
                }
                ,
                oncheck: function(obj){
                    var currentNode=obj.data;
                    var nodeId=currentNode.id;
                    var nodeType=currentNode.type;
                    var checkStatus=obj.checked;
                    if('file'==nodeType)
                    {
                        if(checkStatus)
                        {
                            window.selectSourceCode.set(nodeId,nodeId);
                        }else
                        {
                            window.selectSourceCode.delete(nodeId);
                        }
                    }
                    console.log(window.selectSourceCode);

                }

            });

        });



    });

});