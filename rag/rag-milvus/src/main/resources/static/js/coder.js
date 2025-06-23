$(document).ready(function() {
    // 初始加载首页内容
    loadContent('design_doc');
    // 菜单点击事件处理函数
    $('#menu a').click(function(e) {
        e.preventDefault(); // 阻止默认链接行为
        var content = $(this).data('content'); // 获取data-content属性值
        loadContent(content); // 加载对应的内容
        $(this).parent().parent().children("li").removeClass("clicked");
        $(this).parent().addClass("clicked");
    });
});

function loadContent(content) {
    // 根据content的值加载不同的HTML内容，这里仅为示例，实际应根据需要动态生成或从服务器获取HTML内容。
    $('#content').html('<h2>加载中...</h2>'); // 显示加载中信息，可根据实际需要调整加载动画等UI元素。
    switch(content) {
        case 'demand_doc':
            $('#content').load('./views/demand_doc.html?t='+new Date().getTime());
            break;
        case 'design_doc':
            $('#content').load('./views/design_doc.html?t='+new Date().getTime());
            break;
        case 'coding_rule_doc':
            $('#content').load('./views/coding_rule_doc.html?t='+new Date().getTime());
            break;
        case 'project':
            $('#content').load('./views/project.html?t='+new Date().getTime());
            break;
        case 'tag':
            $('#content').load('./views/tag.html?t='+new Date().getTime());
            break;
        case 'branch':
            $('#content').load('./views/branch.html?t='+new Date().getTime());
            break;
        default:
            $('#content').html('<h2>页面未找到</h2>'); // 如果content值不匹配任何已知情况，显示错误信息。
    }
}

/**
 * post协议，打开新窗口
 * @param url 地址
 * @param data json格式数据包
 */
function openPostWindow(url, data) {
    var form = document.createElement('form');
    form.method = 'POST';
    form.action = url;
    form.target = '_blank'; // 在新窗口中打开

    for (var key in data) {
        if (data.hasOwnProperty(key)) {
            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            input.value = data[key];
            form.appendChild(input);
        }
    }
    document.body.appendChild(form); // 添加到DOM中
    form.submit(); // 提交表单
    document.body.removeChild(form); // 提交后移除表单
    // 发送方
    sessionStorage.setItem('documentsData', JSON.stringify(data));
    // 接收方
    //const data = JSON.parse(sessionStorage.getItem('postData'));
}