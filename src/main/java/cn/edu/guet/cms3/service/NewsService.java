package cn.edu.guet.cms3.service;

import cn.edu.guet.cms3.dto.NewsCreateDTO;
import cn.edu.guet.cms3.util.PageRequest;
import cn.edu.guet.cms3.vo.NewsVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface NewsService {

    NewsVO getNewsDetail(Long id);

    IPage<NewsVO> getNewsPage(PageRequest pageRequest);

    IPage<NewsVO> getPublicNewsPage(PageRequest pageRequest);

    @Transactional(rollbackFor = Exception.class)
    NewsVO createNews(NewsCreateDTO newsCreateDTO);

    @Transactional(rollbackFor = Exception.class)
    void deleteNews(Long id);
    @Transactional(rollbackFor = Exception.class)
    NewsVO approveNews(Long id);

    @Transactional(rollbackFor = Exception.class)
    NewsVO rejectNews(Long id);
    NewsVO createNewsWithFiles(NewsCreateDTO newsCreateDTO, List<MultipartFile> files) throws IOException;


    NewsVO getPublicNewsDetail(Long id);

    NewsVO updateNewsWithFiles(Long id, NewsCreateDTO newsCreateDTO, List<MultipartFile> files) throws IOException;

    @Transactional(rollbackFor = Exception.class)
    NewsVO updateNews(Long id, NewsCreateDTO newsCreateDTO);
}
