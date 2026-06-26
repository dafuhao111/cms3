package cn.edu.guet.cms3.service.impl;

import cn.edu.guet.cms3.dto.NewsCreateDTO;
import cn.edu.guet.cms3.entity.News;
import cn.edu.guet.cms3.mapper.NewsMapper;
import cn.edu.guet.cms3.service.NewsService;
import cn.edu.guet.cms3.util.PageRequest;
import cn.edu.guet.cms3.vo.NewsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import cn.edu.guet.cms3.dto.NewsAttachmentDTO;
import cn.edu.guet.cms3.entity.NewsAttachment;
import cn.edu.guet.cms3.mapper.NewsAttachmentMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsServiceImpl implements NewsService {

    private static final String PENDING_REVIEW = "PENDING_REVIEW";
    private static final String PUBLISHED = "PUBLISHED";
    private static final String REJECTED = "REJECTED";
    @Autowired
    private NewsMapper newsMapper;

    @Override
    public NewsVO getNewsDetail(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new IllegalArgumentException("新闻不存在");
        }
        return toVO(news);
    }


    @Value("${news.upload-dir:/Users/liwei/Desktop/upload}")
    private String uploadDir;

    @Autowired
    private NewsAttachmentMapper newsAttachmentMapper;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public NewsVO updateNewsWithFiles(Long id, NewsCreateDTO newsCreateDTO, List<MultipartFile> files) throws IOException {
        updateNews(id, newsCreateDTO);
        rebuildAttachments(id, newsCreateDTO.getAttachments());
        insertUploadedAttachments(id, files);
        return getNewsDetail(id);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public NewsVO createNewsWithFiles(NewsCreateDTO newsCreateDTO, List<MultipartFile> files) throws IOException {
        NewsVO newsVO = createNews(newsCreateDTO);
        insertUploadedAttachments(Long.valueOf(newsVO.getId()), files);
        return toVO(newsMapper.selectById(newsVO.getId()));
    }
    @Override
    public IPage<NewsVO> getNewsPage(PageRequest pageRequest) {
        Page<News> page = new Page<>(pageRequest.getCurrentPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();

        String category = pageRequest.getParamValue("category");
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(News::getCategory, category);
        }

        String status = pageRequest.getParamValue("status");
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(News::getStatus, status);
        }

        String title = pageRequest.getParamValue("title");
        if (StringUtils.hasText(title)) {
            queryWrapper.like(News::getTitle, title);
        }

        queryWrapper.orderByDesc(News::getCreateTime);

        IPage<News> newsPage = newsMapper.selectPage(page, queryWrapper);

        Page<NewsVO> voPage = new Page<>(newsPage.getCurrent(), newsPage.getSize(), newsPage.getTotal());
        voPage.setRecords(newsPage.getRecords().stream()
                .map(this::toVO)
                .toList());

        return voPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public NewsVO createNews(NewsCreateDTO newsCreateDTO) {
        validateNews(newsCreateDTO);

        News news = new News();
        news.setTitle(newsCreateDTO.getTitle());
        news.setCategory(newsCreateDTO.getCategory());
        news.setSupplier(newsCreateDTO.getSupplier());
        news.setReviewer(newsCreateDTO.getReviewer());
        news.setContent(newsCreateDTO.getContent());
        news.setStatus(PENDING_REVIEW);

        newsMapper.insert(news);

        return getNewsDetail(news.getId());
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public NewsVO updateNews(Long id, NewsCreateDTO newsCreateDTO) {
        if (id == null) {
            throw new IllegalArgumentException("新闻ID不能为空");
        }

        validateNews(newsCreateDTO);

        News existingNews = newsMapper.selectById(id);
        if (existingNews == null) {
            throw new IllegalArgumentException("新闻不存在");
        }

        existingNews.setTitle(newsCreateDTO.getTitle());
        existingNews.setCategory(newsCreateDTO.getCategory());
        existingNews.setSupplier(newsCreateDTO.getSupplier());
        existingNews.setReviewer(newsCreateDTO.getReviewer());
        existingNews.setContent(newsCreateDTO.getContent());
//        existingNews.setStatus(STATUS_PENDING_REVIEW);
        existingNews.setPublishTime(null);
        newsMapper.updateById(existingNews);

        return toVO(newsMapper.selectById(id));
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new IllegalArgumentException("新闻不存在");
        }

        QueryWrapper<NewsAttachment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("news_id", id);
        List<NewsAttachment> attachments = newsAttachmentMapper.selectList(queryWrapper);

        attachments.forEach(this::deleteAttachmentFile);
        newsMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public NewsVO approveNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new IllegalArgumentException("新闻不存在");
        }
        news.setStatus(PUBLISHED);
        news.setPublishTime(LocalDateTime.now());
        newsMapper.updateById(news);
        return getNewsDetail(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public NewsVO rejectNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new IllegalArgumentException("新闻不存在");
        }
        news.setStatus(REJECTED);
        newsMapper.updateById(news);
        return getNewsDetail(id);
    }
    private void validateNews(NewsCreateDTO newsCreateDTO) {
        if (newsCreateDTO == null || !StringUtils.hasText(newsCreateDTO.getTitle())) {
            throw new IllegalArgumentException("新闻标题不能为空");
        }
        if (!StringUtils.hasText(newsCreateDTO.getCategory())) {
            throw new IllegalArgumentException("栏目不能为空");
        }
        if (!StringUtils.hasText(newsCreateDTO.getContent())) {
            throw new IllegalArgumentException("正文内容不能为空");
        }
    }
    private News getReviewableNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new IllegalArgumentException("新闻不存在");
        }
        if (!PENDING_REVIEW.equals(news.getStatus())) {
            throw new IllegalArgumentException("只有待审核新闻可以审核");
        }
        return news;
    }


    private void insertUploadedAttachments(Long newsId, List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            return;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            NewsAttachmentDTO attachmentDTO = uploadAttachment(file);
            NewsAttachment attachment = new NewsAttachment();
            attachment.setNewsId(newsId);
            attachment.setName(attachmentDTO.getName());
            attachment.setUrl(attachmentDTO.getUrl());
            attachment.setFileSize(attachmentDTO.getSize());
            attachment.setFileType(attachmentDTO.getType());
            newsAttachmentMapper.insert(attachment);
        }
    }

    private NewsAttachmentDTO uploadAttachment(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        String originalFilename = Objects.requireNonNullElse(file.getOriginalFilename(), "attachment");
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String storedFilename = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(storedFilename);
        file.transferTo(targetPath);

        NewsAttachmentDTO attachmentDTO = new NewsAttachmentDTO();
        attachmentDTO.setName(originalFilename);
        attachmentDTO.setUrl("/api/news/attachments/" + storedFilename);
        attachmentDTO.setSize(file.getSize());
        attachmentDTO.setType(file.getContentType());
        return attachmentDTO;
    }


    private void rebuildAttachments(Long newsId, List<NewsAttachmentDTO> keptAttachments) {
        QueryWrapper<NewsAttachment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("news_id", newsId);
        List<NewsAttachment> existingAttachments = newsAttachmentMapper.selectList(queryWrapper);

        List<NewsAttachmentDTO> safeKeptAttachments = keptAttachments == null ? Collections.emptyList() : keptAttachments;
        Set<String> keptUrls = safeKeptAttachments.stream()
                .filter(attachment -> attachment != null && StringUtils.hasText(attachment.getUrl()))
                .map(NewsAttachmentDTO::getUrl)
                .collect(Collectors.toSet());

        newsAttachmentMapper.delete(queryWrapper);

        for (NewsAttachmentDTO attachmentDTO : safeKeptAttachments) {
            if (attachmentDTO == null || !StringUtils.hasText(attachmentDTO.getName()) || !StringUtils.hasText(attachmentDTO.getUrl())) {
                continue;
            }
            NewsAttachment attachment = new NewsAttachment();
            attachment.setNewsId(newsId);
            attachment.setName(attachmentDTO.getName());
            attachment.setUrl(attachmentDTO.getUrl());
            attachment.setFileSize(attachmentDTO.getSize());
            attachment.setFileType(attachmentDTO.getType());
            newsAttachmentMapper.insert(attachment);
        }

        existingAttachments.stream()
                .filter(attachment -> !keptUrls.contains(attachment.getUrl()))
                .forEach(this::deleteAttachmentFile);
    }
    private void deleteAttachmentFile(NewsAttachment attachment) {
        if (attachment == null || !StringUtils.hasText(attachment.getUrl())) {
            return;
        }
        String filename = attachment.getUrl().substring(attachment.getUrl().lastIndexOf('/') + 1);
        try {
            Files.deleteIfExists(Paths.get(uploadDir).resolve(filename));
        } catch (IOException exception) {
            log.warn("删除新闻附件文件失败，url={}", attachment.getUrl(), exception);
        }
    }


    private NewsVO toVO(News news) {
        NewsVO newsVO = new NewsVO();
        BeanUtils.copyProperties(news, newsVO);

        QueryWrapper<NewsAttachment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("news_id", news.getId());
        queryWrapper.orderByAsc("id");
        List<NewsAttachmentDTO> attachments = newsAttachmentMapper.selectList(queryWrapper)
                .stream()
                .map(this::toAttachmentDTO)
                .toList();
        newsVO.setAttachments(attachments);

        return newsVO;
    }

    private NewsAttachmentDTO toAttachmentDTO(NewsAttachment attachment) {
        NewsAttachmentDTO attachmentDTO = new NewsAttachmentDTO();
        attachmentDTO.setName(attachment.getName());
        attachmentDTO.setUrl(attachment.getUrl());
        attachmentDTO.setSize(attachment.getFileSize());
        attachmentDTO.setType(attachment.getFileType());
        return attachmentDTO;
    }
    @Override
    public IPage<NewsVO> getPublicNewsPage(PageRequest pageRequest) {
        PageRequest safePageRequest = pageRequest == null ? new PageRequest() : pageRequest;
        Page<News> page = new Page<>(safePageRequest.getCurrentPage(), safePageRequest.getPageSize());
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("status", PUBLISHED);

        String keyword = safePageRequest.getParamValue("keyword");
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like("title", keyword)
                    .or()
                    .like("supplier", keyword)
                    .or()
                    .like("reviewer", keyword));
        }

        String category = safePageRequest.getParamValue("category");
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
        }

        queryWrapper.orderByDesc("publish_time", "update_time", "id");

        return newsMapper.selectPage(page, queryWrapper).convert(this::toVO);
    }
    @Override
    public NewsVO getPublicNewsDetail(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null || !PUBLISHED.equals(news.getStatus())) {
            throw new IllegalArgumentException("新闻不存在或尚未发布");
        }

        return toVO(news);
    }
}

