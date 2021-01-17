const changeArticleSubmit = (type) => {
    console.log("click")
    let changeArticleForm = document.form_change_article
    let checks = document.getElementsByName("article_check")
    let articleId = null

    for (let check of checks) {
        if (check.checked) {
            articleId = check.getAttribute("data-id")
            break;
        }
    }
    if(articleId === null) {
        alert("記事を選択してください")
        return
    }
    switch (type) {
        case "update":
            changeArticleForm.action = `/edit/${articleId}`
            changeArticleForm.submit()
            break
        case "delete":
            changeArticleForm.action = `/delete/confirm/${articleId}`
            changeArticleForm.submit()
        default:
            break
    }
}