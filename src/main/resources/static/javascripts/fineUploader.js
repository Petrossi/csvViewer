var manualUploader = new qq.FineUploader({
    element: document.getElementById('fine-uploader-manual-trigger'),
    template: 'qq-template-manual-trigger',
    request: {
        // endpoint: 'http://localhost:9090/uploads'
        endpoint: '/uploads'
    },
    thumbnails: {
        placeholders: {
            waitingPath: 'https://fineuploader.com/source/placeholders/waiting-generic.png',
            notAvailablePath: 'https://fineuploader.com/source/placeholders/not_available-generic.png'
        }
    },
    autoUpload: false,
    debug: true
});

qq(document.getElementById("trigger-upload")).attach("click", function() {
    manualUploader.uploadStoredFiles();
});