/** @type {import('tailwindcss').Config} */
module.exports = {
    // 关键：添加 Vue 组件、HTML 文件路径
    content: [
        "./index.html",          // 项目根目录的 index.html
        "./src/**/*.{vue,js,ts}" // src 文件夹下所有 .vue/.js/.ts 文件
    ],
    theme: {
        extend: {}, // 后续想自定义颜色、字体，就在这里加
    },
    plugins: [],
}