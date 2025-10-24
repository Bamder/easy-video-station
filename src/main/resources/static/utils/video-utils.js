function playHls(playerId, videoName, setStatusFunc = null, paramList = null, showErrorFunc = null) {
    const video = document.getElementById(playerId);
    const url = `/api/streams/${encodeURIComponent(videoName)}`;

     // 重置状态
    video.src = "";
    if(setStatusFunc && paramList && paramList.length > 0)
        try{
            setStatusFunc(...paramList[0]);
        }catch(e){
            console.error('[playHls] setStatusFunc 调用失败:', e);
        }

    // 非Safari用hls.js第三方模块
    if (window.Hls && Hls.isSupported()) {
        const hls = new Hls();

        hls.on(Hls.Events.MEDIA_ATTACHED, () => {
            if(setStatusFunc && paramList && paramList.length > 1)
                setStatusFunc(...paramList[1]);
        });

        hls.on(Hls.Events.MANIFEST_PARSED, () => {
        if(setStatusFunc && paramList && paramList.length > 2)
            try{
                setStatusFunc(...paramList[2]);
            }catch(e){
                console.error('[playHls] setStatusFunc 调用失败:', e);
            }

            video.play().catch(()=>{
                console.error('[playHls] 视频播放失败');
            });
        });

        hls.on(Hls.Events.ERROR, (evt, data) => {
            const code = data && data.response ? (data.response.code ?? data.response.status) : undefined;
            if (showErrorFunc) {
                switch(code){
                    case 404:
                        showErrorFunc('视频不存在');
                        break;
                    case 500:
                        showErrorFunc('服务器内部错误');
                        break;
                    default:
                        showErrorFunc(`HLS错误: ${data.type}${data.details ? ' - ' + data.details : ''}`);
                        break;
                }
            } else {
                console.error('[playHls] HLS错误', data);
            }

            if (data.fatal) { hls.destroy(); }
        });

        hls.loadSource(url);
        hls.attachMedia(video);
    } else {
        // Safari/iOS使用原生支持的HLS
        video.src = url;

        if(setStatusFunc && paramList && paramList.length > 3)
            try{
                setStatusFunc(...paramList[3]);
            }catch(e){
                console.error('[playHls] setStatusFunc 调用失败:', e);
            }
        
        video.play().catch(()=>{
            console.error('[playHls] 视频播放失败');
        });
    }
}


export default {
    playHls,
};
