const manualUploader = new qq.FineUploader({
    element: document.getElementById('fine-uploader-manual-trigger'),
    template: 'qq-template-manual-trigger',
    request: {
        endpoint: '/uploads'
    },
    thumbnails: {
        placeholders: {
            waitingPath: 'https://fineuploader.com/source/placeholders/waiting-generic.png',
            notAvailablePath: 'https://fineuploader.com/source/placeholders/not_available-generic.png'
        }
    },
    chunking: {
        enabled: true,
        concurrent: {
            enabled: true
        },
        success: {
            endpoint: '/chunksdone'
        }
    },
    callbacks: {
        onComplete: function(id, name, responseJSON, ) {

        }
    },
    validation: {
        itemLimit: 1,
    },
    autoUpload: false,
    debug: true
});

qq(document.getElementById("trigger-upload")).attach("click", function() {
    manualUploader.uploadStoredFiles();
});