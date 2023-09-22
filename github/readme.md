- 推荐设置为base64发送
    - template-github.txt 是文本版本
        - 变量没有：64x64Png和launchAppImageUrl

    - template-github.html 是 HTML 版本

- 变量规范: {{name}}

1. applicationName: 引用名称
2. applicationDescription：应用描述，模板="Once completed, you can start using all of {{applicationName}}'s features to do sth"。比如browse posts, read articles, ask or solve questions.
3. logo64x64Url: 64x64 logo图片
4. username: 用户名
5. verificationCode：验证码
6. launchAppImageUrl: 开启启动一个应用的图标, 可以是./mona-launch-rocket.png 这张图片
7. inputEmailCodePageUrl: 打开输入验证码的页面, 如果不需要, 连带的 table 标签都删除掉
8. verifyEmailByClickLinkUrl: 点击 url 来验证邮箱, 同样, 不需要删除文字
9. companyContactInformation: 公司联系信息, 没有删除. 比如公司类型 ・地区 ・街道 邮政编码
