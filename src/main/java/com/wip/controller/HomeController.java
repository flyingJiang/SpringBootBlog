package com.wip.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.vdurmont.emoji.EmojiParser;
import com.wip.api.QiNiuCloudService;
import com.wip.constant.ErrorConstant;
import com.wip.constant.Types;
import com.wip.constant.WebConst;
import com.wip.dto.MetaDto;
import com.wip.dto.ParamDTO;
import com.wip.dto.cond.ContentCond;
import com.wip.exception.BusinessException;
import com.wip.model.AttAchDomain;
import com.wip.model.CommentDomain;
import com.wip.model.ContentDomain;
import com.wip.model.MetaDomain;
import com.wip.model.UserDomain;
import com.wip.service.article.ContentService;
import com.wip.service.attach.AttAchService;
import com.wip.service.comment.CommentService;
import com.wip.service.meta.MetaService;
import com.wip.utils.APIResponse;
import com.wip.utils.IPKit;
import com.wip.utils.TaleUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Api("博客前台页面")
@Slf4j
@Controller
public class HomeController extends BaseController {
//    public static final String CLASSPATH = TaleUtils.getUploadFilePath();

    @Autowired
    private AttAchService attAchService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MetaService metaService;


    @GetMapping(value = "/")
    public String index(
            HttpServletRequest request,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam(name = "page", required = false, defaultValue = "1")
                    int page,
            @ApiParam(name = "limit", value = "每页数量", required = false)
            @RequestParam(name = "limit", required = false, defaultValue = "5")
                    int limit
    ) {
        PageInfo<ContentDomain> articles = contentService.getArticlesByCond(new ContentCond(), page, limit);

        request.setAttribute("articles", articles);
        return "blog/home";
    }


//    @GetMapping(value = "/app")
//    public String toApp(HttpServletRequest request) {
//        log.info("appIndex");
//        return "blog/appIndex";
//    }

    @GetMapping(value = "/index")
    public String toIndex(HttpServletRequest request) {
        log.info("toIndex");
        return "blog/index";
    }

    @GetMapping(value = "/see")
    public String toSee(HttpServletRequest request) {
        log.info("toSee");
        return "blog/see";
    }

    @GetMapping(value = "/see2")
    public String toSee2(HttpServletRequest request) {
        log.info("toSee2");
        return "blog/see2";
    }

    @GetMapping(value = "/ask")
    public String toAsk(HttpServletRequest request) {
        log.info("toAsk");
        return "blog/ask";
    }

    @GetMapping(value = "/ask2")
    public String toAsk2(HttpServletRequest request) {
        log.info("toAsk2");
        return "blog/ask2";
    }

    @GetMapping(value = "/report")
    public String toReport(HttpServletRequest request) {
        log.info("toReport");
        return "blog/report";
    }

    @GetMapping(value = "/recommend")
    public String recommend(HttpServletRequest request) {
        log.info("toRecommend");
        return "blog/recommend";
    }

    @GetMapping(value = "/ask_to_report")
    public String ask_to_report(HttpServletRequest request) {
        log.info("ask_to_report");
        return "blog/ask_to_report";
    }


    @ApiOperation("多文件上传")
    @PostMapping(value = "upload")
    @ResponseBody
    public APIResponse filesUploadToCloud(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(name = "file", value = "文件数组", required = true)
            @RequestParam(name = "file", required = true)
                    MultipartFile[] files
    ) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type", "text/html");

            for (MultipartFile file : files) {

                String fileName = TaleUtils.getFileKey(file.getOriginalFilename().replaceFirst("/", ""));

                QiNiuCloudService.upload(file, fileName);
                AttAchDomain attAchDomain = new AttAchDomain();
                HttpSession session = request.getSession();
//                UserDomain sessionUser = (UserDomain) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
                attAchDomain.setAuthorId(1);
                attAchDomain.setFtype(TaleUtils.isImage(file.getInputStream()) ? Types.IMAGE.getType() : Types.FILE.getType());
                attAchDomain.setFname(fileName);
                attAchDomain.setFkey(QiNiuCloudService.QINIU_UPLOAD_SITE + fileName);
                attAchService.addAttAch(attAchDomain);
            }
            return APIResponse.success();

        } catch (IOException e) {
            e.printStackTrace();
            throw BusinessException.withErrorCode(ErrorConstant.Att.ADD_NEW_ATT_FAIL)
                    .withErrorMessageArguments(e.getMessage());
        }

    }

    /**
     * http://localhost:8888/info_gather?history=demoData&inp=
     * http://localhost:8888/info_gather
     */
    @ApiOperation("info_gather")
    @PostMapping(value = "/info_gather")
    @ResponseBody
    public APIResponse info_gather(HttpServletRequest request, @RequestBody ParamDTO param) {
        log.info("param = {}", JSONUtil.toJsonStr(param));
//        return APIResponse.success("go_to_next");
        String requestUrl = "http://10.41.212.95:8000/info_gather/1";
        String resultJson4 =
                HttpRequest.post(requestUrl)
                        .body(JSONUtil.toJsonStr(param), "application/json")
                        .execute().body();
        log.info("resultJson4 = {}", resultJson4);
        if (StringUtils.isNotBlank(resultJson4)) {
            if (resultJson4.contains("medical_history")) {
                // 结束了
                return APIResponse.success("go_to_next");
            }
            resultJson4 = resultJson4.replaceAll("\"", "");

        }

        return APIResponse.success(resultJson4);
    }


    @ApiOperation("归档内容页")
    @GetMapping(value = "/archives")
    public String archives(
            HttpServletRequest request,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam(name = "page", required = false, defaultValue = "1")
                    int page,
            @ApiParam(name = "limit", value = "每页数量", required = false)
            @RequestParam(name = "limit", required = false, defaultValue = "10")
                    int limit
    ) {
        PageInfo<ContentDomain> articles = contentService.getArticlesByCond(new ContentCond(), page, limit);
        request.setAttribute("articles", articles);
        return "blog/archives";
    }

    @ApiOperation("分类内容页")
    @GetMapping(value = "/categories")
    public String categories(HttpServletRequest request) {
        // 获取分类
        List<MetaDto> categories = metaService.getMetaList(Types.CATEGORY.getType(), null, WebConst.MAX_POSTS);
        // 分类总数
        Long categoryCount = metaService.getMetasCountByType(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        request.setAttribute("categoryCount", categoryCount);
        return "blog/category";
    }

    @ApiOperation("分类详情页")
    @GetMapping(value = "/categories/{name}")
    public String categoriesDetail(
            HttpServletRequest request,
            @ApiParam(name = "name", value = "分类名称", required = true)
            @PathVariable("name")
                    String name
    ) {
        MetaDomain category = metaService.getMetaByName(Types.CATEGORY.getType(), name);
        if (null == category.getName()) {
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        }
        List<ContentDomain> articles = contentService.getArticleByCategory(category.getName());
        request.setAttribute("category", category.getName());
        request.setAttribute("articles", articles);
        return "blog/category_detail";
    }

    @ApiOperation("标签内容页")
    @GetMapping(value = "/tags")
    public String tags(HttpServletRequest request) {
        // 获取标签
        List<MetaDto> tags = metaService.getMetaList(Types.TAG.getType(), null, WebConst.MAX_POSTS);
        // 标签总数
        Long tagCount = metaService.getMetasCountByType(Types.TAG.getType());
        request.setAttribute("tags", tags);
        request.setAttribute("tagCount", tagCount);
        return "blog/tags";
    }

    @ApiOperation("标签详情页")
    @GetMapping(value = "/tags/{name}")
    public String tagsDetail(
            HttpServletRequest request,
            @ApiParam(name = "name", value = "标签名", required = true)
            @PathVariable("name")
                    String name
    ) {
        MetaDomain tags = metaService.getMetaByName(Types.TAG.getType(), name);
        List<ContentDomain> articles = contentService.getArticleByTags(tags);
        request.setAttribute("articles", articles);
        request.setAttribute("tags", tags.getName());
        return "blog/tags_detail";
    }

    @GetMapping(value = "/about")
    public String about() {
        return "blog/about";
    }

    @ApiOperation("文章内容页")
    @GetMapping(value = "/detail/{cid}")
    public String detail(
            @ApiParam(name = "cid", value = "文章主键", required = true)
            @PathVariable("cid")
                    Integer cid,
            HttpServletRequest request
    ) {
        ContentDomain article = contentService.getArticleById(cid);
        request.setAttribute("article", article);

        // 更新文章的点击量
        this.updateArticleHits(article.getCid(), article.getHits());
        // 获取评论
        List<CommentDomain> comments = commentService.getCommentsByCId(cid);
        request.setAttribute("comments", comments);

        return "blog/detail";
    }

    /**
     * 更新文章的点击率
     *
     * @param cid
     * @param chits
     */
    private void updateArticleHits(Integer cid, Integer chits) {
        Integer hits = cache.hget("article", "hits");
        if (chits == null) {
            chits = 0;
        }
        hits = null == hits ? 1 : hits + 1;
        if (hits >= WebConst.HIT_EXEED) {
            ContentDomain temp = new ContentDomain();
            temp.setCid(cid);
            temp.setHits(chits + hits);
            contentService.updateContentByCid(temp);
            cache.hset("article", "hits", 1);
        } else {
            cache.hset("article", "hits", hits);
        }

    }

    @PostMapping(value = "/comment")
    @ResponseBody
    public APIResponse comment(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(name = "cid", required = true) Integer cid,
                               @RequestParam(name = "coid", required = false) Integer coid,
                               @RequestParam(name = "author", required = false) String author,
                               @RequestParam(name = "email", required = false) String email,
                               @RequestParam(name = "url", required = false) String url,
                               @RequestParam(name = "content", required = true) String content,
                               @RequestParam(name = "csrf_token", required = true) String csrfToken
    ) {

        String ref = request.getHeader("Referer");
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(csrfToken)) {
            return APIResponse.fail("访问失败");
        }

        String token = cache.hget(Types.CSRF_TOKEN.getType(), csrfToken);
        if (StringUtils.isBlank(token)) {
            return APIResponse.fail("访问失败");
        }

        if (null == cid || StringUtils.isBlank(content)) {
            return APIResponse.fail("请输入完整后评论");
        }

        if (StringUtils.isNotBlank(author) && author.length() > 50) {
            return APIResponse.fail("姓名过长");
        }

        if (StringUtils.isNotBlank(email) && !TaleUtils.isEmail(email)) {
            return APIResponse.fail("请输入正确的邮箱格式");
        }

        if (StringUtils.isNotBlank(url) && !TaleUtils.isURL(url)) {
            return APIResponse.fail("请输入正确的网址格式");
        }

        if (content.length() > 200) {
            return APIResponse.fail("请输入200个字符以内的评价");
        }

        String val = IPKit.getIpAddressByRequest1(request) + ":" + cid;
        Integer count = cache.hget(Types.COMMENTS_FREQUENCY.getType(), val);
        if (null != count && count > 0) {
            return APIResponse.fail("您发表的评论太快了，请过会再试");
        }

        author = TaleUtils.cleanXSS(author);
        content = TaleUtils.cleanXSS(content);

        author = EmojiParser.parseToAliases(author);
        content = EmojiParser.parseToAliases(content);


        CommentDomain comments = new CommentDomain();
        comments.setAuthor(author);
        comments.setCid(cid);
        comments.setIp(request.getRemoteAddr());
        comments.setUrl(url);
        comments.setContent(content);
        comments.setEmail(email);
        comments.setParent(coid);

        try {
            commentService.addComment(comments);
            cookie("tale_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
            cookie("tale_remember_mail", URLEncoder.encode(email, "UTF-8"), 7 * 24 * 60 * 60, response);
            if (StringUtils.isNotBlank(url)) {
                cookie("tale_remember_url", URLEncoder.encode(url, "UTF-8"), 7 * 24 * 60 * 60, response);
            }
            // 设置对每个文章1分钟可以评论一次
            cache.hset(Types.COMMENTS_FREQUENCY.getType(), val, 1, 60);

            return APIResponse.success();

        } catch (Exception e) {
            throw BusinessException.withErrorCode(ErrorConstant.Comment.ADD_NEW_COMMENT_FAIL);
        }

    }

    private void cookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }


}
