import videoUtils from './utils/video-utils.js';
document.addEventListener('DOMContentLoaded', () => {
    const playHls = videoUtils.playHls;
    // 元素引用
    const videoPlayer = document.getElementById('videoPlayer');
    const videoInput = document.getElementById('videoInput');
    const playBtn = document.getElementById('playBtn');
    const statusIndicator = document.getElementById('statusIndicator');
    const statusText = document.getElementById('statusText');
    const errorMessage = document.getElementById('errorMessage');
    
    // 状态管理
    function setStatus(connected, message) {
        statusIndicator.className = 'indicator' + (connected ? ' connected' : '');
        statusText.textContent = message;
    }
    
    // 错误消息
    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.classList.remove('hidden');
        setTimeout(() => {
            errorMessage.classList.add('hidden');
        }, 5000);
    }
    
    // 播放按钮事件处理
    playBtn.addEventListener('click', () => {

        const videoName = videoInput.value.trim();
        if (!videoName) {
            showError('请输入视频名称');
            return;
        }
        playHls('videoPlayer', videoName, setStatus, [[
            false, '正在载入'
        ],[
            true, '已连接'
        ],[
            true, '清单解析完成'
        ],[
            true, '原生HLS就绪'
        ]], showError);
    });
    

    videoInput.addEventListener('keypress', (e) => { //空格播放
        if (e.key === 'Space') {
            playBtn.click();
        }
    });
    
    // 添加进度显示更新
    videoPlayer.addEventListener('timeupdate', function() {
        const progress = (this.currentTime / this.duration) * 100;
        if (!isNaN(progress)) {
            statusText.textContent = `播放中: ${progress.toFixed(1)}%`;
        }
    });
});