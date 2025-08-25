document.addEventListener("DOMContentLoaded", () => {
    const { username, conversationId, contextPath } = window.chatConfig;
    let ws = null;
    let stream = null;

    const chatWindow = document.getElementById("chat-window");
    const messageInput = document.getElementById("message");
    const sendButton = document.getElementById("send-button");

    //WS Connection
    function connectWebSocket() {
        const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
        const host = window.location.host;
        const wsUrl = `${protocol}//${host}/${contextPath}/chat-app/chat/${conversationId}`;

        ws = new WebSocket(wsUrl);

        ws.onopen = () => console.log("Connected to chat server");
        ws.onclose = () => {
            console.log("Disconnected, retrying...");
            setTimeout(connectWebSocket, 1000);
        };
        ws.onerror = err => console.error("WS error:", err);

        ws.onmessage = event => {
            try {
                const data = JSON.parse(event.data);
                const now = new Date();
                const timeString = `${now.getHours()}:${String(now.getMinutes()).padStart(2, "0")}`;

                const messageElement = document.createElement("div");
                messageElement.className = "message received";

                if (data.type === "text") {
                    messageElement.innerHTML = `
                        <div class="message-sender">${data.sender}</div>
                        <div class="message-text">${data.message}</div>
                        <div class="message-time">${timeString}</div>
                    `;
                } else if (data.type === "image") {
                    messageElement.innerHTML = `
                        <div class="message-sender">${data.sender}</div>
                        <div class="message-text">
                            <div>Photo</div>
                            <img src="${data.data}" class="message-image" alt="Received photo">
                        </div>
                        <div class="message-time">${timeString}</div>
                    `;
                }

                chatWindow.appendChild(messageElement);
                chatWindow.scrollTop = chatWindow.scrollHeight;
            } catch (e) {
                console.error("Parse error:", e);
            }
        };
    }
    connectWebSocket();

    // Messages
    function sendMessage() {
        const text = messageInput.value.trim();
        if (text === "") return;

        const now = new Date();
        const timeString = `${now.getHours()}:${String(now.getMinutes()).padStart(2, "0")}`;

        const messageElement = document.createElement("div");
        messageElement.className = "message sent";
        messageElement.innerHTML = `
            <div class="message-text">${text}</div>
            <div class="message-time">${timeString}</div>
        `;
        chatWindow.appendChild(messageElement);
        chatWindow.scrollTop = chatWindow.scrollHeight;

        const payload = { type: "text", sender: username, message: text, conversationId };
        if (ws && ws.readyState === WebSocket.OPEN) ws.send(JSON.stringify(payload));

        messageInput.value = "";
    }

    sendButton.addEventListener("click", sendMessage);
    messageInput.addEventListener("keypress", e => {
        if (e.key === "Enter") sendMessage();
    });

    //Camera
    const cameraModal = document.getElementById("camera-modal");
    const cameraButton = document.getElementById("camera-button");
    const cancelCameraButton = document.getElementById("cancel-camera");
    const captureButton = document.getElementById("capture-btn");
    const video = document.getElementById("video");
    const canvas = document.getElementById("canvas");
    const ctx = canvas.getContext("2d");

    async function startCamera() {
        try {
            stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: false });
            video.srcObject = stream;
        } catch (err) {
            console.error("Camera error:", err);
            alert("Could not access camera. Please check permissions.");
        }
    }
    function stopCamera() {
        if (stream) stream.getTracks().forEach(track => track.stop());
    }
    function capturePhoto() {
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        const imageData = canvas.toDataURL("image/png");
        stopCamera();
        cameraModal.style.display = "none";
        sendImageMessage(imageData);
    }
    function sendImageMessage(imageData) {
        const now = new Date();
        const timeString = `${now.getHours()}:${String(now.getMinutes()).padStart(2, "0")}`;

        const messageElement = document.createElement("div");
        messageElement.className = "message sent";
        messageElement.innerHTML = `
            <div class="message-text">
                <div>Photo</div>
                <img src="${imageData}" class="message-image" alt="Sent photo">
            </div>
            <div class="message-time">${timeString}</div>
        `;
        chatWindow.appendChild(messageElement);
        chatWindow.scrollTop = chatWindow.scrollHeight;

        if (ws && ws.readyState === WebSocket.OPEN) {
            ws.send(JSON.stringify({ type: "image", data: imageData, sender: username, conversationId }));
        }
    }

    cameraButton.addEventListener("click", () => {
        cameraModal.style.display = "flex";
        startCamera();
    });
    cancelCameraButton.addEventListener("click", () => {
        stopCamera();
        cameraModal.style.display = "none";
    });
    captureButton.addEventListener("click", capturePhoto);

    //clean
    window.addEventListener("beforeunload", () => {
        if (stream) stopCamera();
        if (ws) ws.close();
    });
});


