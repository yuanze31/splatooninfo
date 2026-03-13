/**
移除无用布局
*/
// 选择所有符合条件的 div 元素
var divs = document.querySelectorAll('div[data-v-00dcc232]');
var previousVisible = false; // 记录前一个元素是否已设置宽度
var lastVisibleDiv = null;   // 保存最后一个被处理的 div

for (var i = 0; i < divs.length; i++) {
    if (previousVisible) {
        // 如果前一个符合条件且已经保留一个宽度，则隐藏当前元素
        divs[i].style.display = 'none';
    } else {
        // 设置当前元素的宽度为 5px，并保留它
        divs[i].style.display = 'block'; // 确保显示
//        divs[i].style.width = '100%';   // 可根据需要调整宽度
        divs[i].style.height = '15px';   // 设置高度
        divs[i].style.backgroundColor = 'transparent'; // 设置背景透明
        divs[i].style.border = 'none';  // 可选：移除边框等
        divs[i].style.margin = '0';     // 移除多余的间距

        // 更新记录状态
        previousVisible = true;
        lastVisibleDiv = divs[i];
    }

    // 如果下一个元素不是符合条件的元素，则重置状态
    if (i + 1 < divs.length && divs[i + 1].dataset.v !== divs[i].dataset.v) {
        previousVisible = false;
    }
}

