module.exports = {
    outputDir: "../src/main/resources/static",      //npm run bulid로 빌드 시 파일이 생성되는 위치
    indexPath: "../static/index.html",              //index.html 파일이 생성될 위치
    devServer: {
        proxy: "http://localhost:8080"
    },
    chainWebpack: config => {
        const svgRule = config.module.rule("svg");
        svgRule.uses.clear();
        svgRule.use("vue-svg-loader").loader("vue-svg-loader");
    }
};