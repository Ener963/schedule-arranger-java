'use strict';

// entry.js 相当（jQuery + bootstrap バンドルの代わりに素のJS + BootstrapのCDN版で実装）。
// Bootstrap本体（ナビゲーションバーのトグルボタン等）は layout.html で CDN から読み込み済み。
// Spring Security の CSRF 保護に対応するため、layout.html の meta タグから
// トークンを読み取ってリクエストヘッダに付与する。

(function () {
    function csrfHeaders() {
        var tokenMeta = document.querySelector('meta[name="_csrf"]');
        var headerMeta = document.querySelector('meta[name="_csrf_header"]');
        var headers = { 'Content-Type': 'application/json' };
        if (tokenMeta && headerMeta) {
            headers[headerMeta.content] = tokenMeta.content;
        }
        return headers;
    }

    var availabilityLabels = ['欠', '？', '出'];
    var buttonStyles = ['btn-danger', 'btn-secondary', 'btn-success'];

    document.querySelectorAll('.availability-toggle-button').forEach(function (button) {
        button.addEventListener('click', function () {
            var scheduleId = button.dataset.scheduleId;
            var userId = button.dataset.userId;
            var candidateId = button.dataset.candidateId;
            var availability = parseInt(button.dataset.availability, 10);
            var nextAvailability = (availability + 1) % 3;

            fetch(
                '/schedules/' + scheduleId + '/users/' + userId + '/candidates/' + candidateId,
                {
                    method: 'POST',
                    headers: csrfHeaders(),
                    body: JSON.stringify({ availability: nextAvailability }),
                },
            )
                .then(function (response) {
                    return response.json();
                })
                .then(function (data) {
                    button.dataset.availability = data.availability;
                    button.textContent = availabilityLabels[data.availability];

                    buttonStyles.forEach(function (style) {
                        button.classList.remove(style);
                    });
                    button.classList.add(buttonStyles[data.availability]);
                });
        });
    });

    var commentButton = document.getElementById('self-comment-button');
    if (commentButton) {
        commentButton.addEventListener('click', function () {
            var scheduleId = commentButton.dataset.scheduleId;
            var userId = commentButton.dataset.userId;
            var comment = window.prompt('コメントを255文字以内で入力してください。');
            if (comment) {
                fetch('/schedules/' + scheduleId + '/users/' + userId + '/comments', {
                    method: 'POST',
                    headers: csrfHeaders(),
                    body: JSON.stringify({ comment: comment }),
                })
                    .then(function (response) {
                        return response.json();
                    })
                    .then(function (data) {
                        var selfComment = document.getElementById('self-comment');
                        if (selfComment) {
                            selfComment.textContent = data.comment;
                        }
                    });
            }
        });
    }

    var deleteScheduleForm = document.getElementById('delete-schedule-form');
    if (deleteScheduleForm) {
        deleteScheduleForm.addEventListener('submit', function (e) {
            e.preventDefault();
            if (window.confirm('本当にこの予定を削除しますか?')) {
                deleteScheduleForm.submit();
            }
        });
    }
})();