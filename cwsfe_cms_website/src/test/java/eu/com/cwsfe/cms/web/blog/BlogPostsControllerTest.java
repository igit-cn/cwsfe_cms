package eu.com.cwsfe.cms.web.blog;

import eu.com.cwsfe.cms.dao.*;
import eu.com.cwsfe.cms.domains.BlogPostI18nContentStatus;
import eu.com.cwsfe.cms.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.anything;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:cwsfe-cms-controller-test.xml"})
@WebAppConfiguration
public class BlogPostsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BlogKeywordsDAO blogKeywordsDAO;
    @Mock
    private BlogPostKeywordsDAO blogPostKeywordsDAO;
    @Mock
    private BlogPostI18nContentsDAO blogPostI18nContentsDAO;
    @Mock
    private BlogPostsDAO blogPostsDAO;
    @Mock
    private CmsAuthorsDAO cmsAuthorsDAO;
    @Mock
    private CmsLanguagesDAO cmsLanguagesDAO;

    @InjectMocks
    private BlogPostsController blogPostsController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(blogPostsController).build();
    }

    @Test
    public void testDefaultView() throws Exception {
        mockMvc.perform(get("/blogPosts"))
                .andExpect(status().isOk())
                .andExpect(view().name("cms/blog/Posts"))
                .andExpect(model().attribute("mainJavaScript", "/resources-cwsfe-cms/js/cms/blog/Posts.js"))
                .andExpect(model().attribute("breadcrumbs", anything()));
    }

    @Test
    public void testListBlogPosts() throws Exception {
        String sEcho = "1";
        int numberOfPosts = 1;
        int ajaxCountNumber = 4;
        int id = 2;
        Date creationDate = new Date(1);
        String author = "author";
        String postTextCode = "postTextCode";
        List<Object[]> cmsPosts = new ArrayList<>();
        Object[] post = new Object[6];
        post[0] = id;
        post[1] = author;
        post[2] = postTextCode;
        post[3] = creationDate;
        cmsPosts.add(post);
        when(blogPostsDAO.searchByAjax(anyInt(), anyInt(), anyInt(), anyString())).thenReturn(cmsPosts);
        when(blogPostsDAO.searchByAjaxCount(anyInt(), anyString())).thenReturn(ajaxCountNumber);
        when(blogPostsDAO.getTotalNumberNotDeleted()).thenReturn(numberOfPosts);

        ResultActions resultActions = mockMvc.perform(get("/blogPostsList")
                .param("iDisplayStart", "0")
                .param("iDisplayLength", "2")
                .param("sEcho", sEcho))
                .andExpect(status().isOk());

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$.sEcho").value(sEcho))
                .andExpect(jsonPath("$.iTotalRecords").value(numberOfPosts))
                .andExpect(jsonPath("$.iTotalDisplayRecords").value(ajaxCountNumber))
                .andExpect(jsonPath("$.aaData").exists())
                .andExpect(jsonPath("$.aaData[0].#").value(1))
                .andExpect(jsonPath("$.aaData[0].author").value(author))
                .andExpect(jsonPath("$.aaData[0].postTextCode").value(postTextCode))
                .andExpect(jsonPath("$.aaData[0].postCreationDate").value("1970-01-01"))
                .andExpect(jsonPath("$.aaData[0].id").value(id));
        verify(blogPostsDAO, times(1)).searchByAjax(anyInt(), anyInt(), anyInt(), anyString());
        verify(blogPostsDAO, times(1)).searchByAjaxCount(anyInt(), anyString());
        verify(blogPostsDAO, times(1)).getTotalNumberNotDeleted();
        verifyNoMoreInteractions(blogPostsDAO);
    }

    @Test
    public void testAddBlogPost() throws Exception {
        when(blogPostsDAO.add(any(BlogPost.class))).thenReturn(1l);

        ResultActions resultActions = mockMvc.perform(post("/addBlogPost")
                .param("postAuthorId", "2")
                .param("postTextCode", "postTextCode"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$." + BlogPostsController.JSON_STATUS).value(BlogPostsController.JSON_STATUS_SUCCESS));
        verify(blogPostsDAO, times(1)).add(any(BlogPost.class));
        verifyNoMoreInteractions(blogPostsDAO);
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(blogPostsDAO).delete(any(BlogPost.class));

        ResultActions resultActions = mockMvc.perform(post("/deleteBlogPost")
                .param("id", "1"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$." + BlogPostsController.JSON_STATUS).value(BlogPostsController.JSON_STATUS_SUCCESS));
        verify(blogPostsDAO, times(1)).delete(any(BlogPost.class));
        verifyNoMoreInteractions(blogPostsDAO);
    }

    @Test
    public void testBrowseBlogPost() throws Exception {
        when(blogPostsDAO.get(anyLong())).thenReturn(new BlogPost());
        when(cmsLanguagesDAO.listAll()).thenReturn(new ArrayList<>());
        when(blogPostKeywordsDAO.listForPost(anyLong())).thenReturn(new ArrayList<>());
        when(blogKeywordsDAO.list()).thenReturn(new ArrayList<>());
        when(cmsAuthorsDAO.get(anyLong())).thenReturn(new CmsAuthor());

        ResultActions resultActions = mockMvc.perform(get("/blogPosts/1"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("cms/blog/SingleBlogPost"))
                .andExpect(model().attribute("mainJavaScript", "/resources-cwsfe-cms/js/cms/blog/SinglePost.js"))
                .andExpect(model().attribute("breadcrumbs", anything()))
                .andExpect(model().attribute("cmsLanguages", anything()))
                .andExpect(model().attribute("blogPostSelectedKeywords", anything()))
                .andExpect(model().attribute("blogKeywords", anything()))
                .andExpect(model().attribute("blogPost", anything()))
                .andExpect(model().attribute("cmsAuthor", anything()));
    }

    @Test
    public void testGetBlogPostI18n() throws Exception {
        BlogPostI18nContent blogPostI18nContent = new BlogPostI18nContent();
        String postTitle = "postTitle";
        String postShortcut = "postShortcut";
        String postDescription = "postDescription";
        blogPostI18nContent.setPostTitle(postTitle);
        blogPostI18nContent.setPostShortcut(postShortcut);
        blogPostI18nContent.setPostDescription(postDescription);
        blogPostI18nContent.setStatus(BlogPostI18nContentStatus.NEW);
        when(blogPostI18nContentsDAO.getByLanguageForPost(anyLong(), anyLong())).thenReturn(blogPostI18nContent);

        ResultActions resultActions = mockMvc.perform(get("/blogPosts/1/2")).andExpect(status().isOk());

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$." + BlogPostsController.JSON_STATUS).value(BlogPostsController.JSON_STATUS_SUCCESS))
                .andExpect(jsonPath("$.data.postTitle").value(postTitle))
                .andExpect(jsonPath("$.data.postShortcut").value(postShortcut))
                .andExpect(jsonPath("$.data.postDescription").value(postDescription))
                .andExpect(jsonPath("$.data.status").value(BlogPostI18nContentStatus.NEW.name()));

        verify(blogPostI18nContentsDAO, times(1)).getByLanguageForPost(anyLong(), anyLong());
        verifyNoMoreInteractions(blogPostI18nContentsDAO);
    }

    @Test
    public void testUpdatePostBasicInfo() throws Exception {
        doNothing().when(blogPostsDAO).updatePostBasicInfo(any(BlogPost.class));

        ResultActions resultActions = mockMvc.perform(post("/blogPosts/updatePostBasicInfo")
                .param("id", "3")
                .param("postTextCode", "postTextCode")
                .param("status", "NEW"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$." + BlogPostsController.JSON_STATUS).value(BlogPostsController.JSON_STATUS_SUCCESS));
        verify(blogPostsDAO, times(1)).updatePostBasicInfo(any(BlogPost.class));
        verifyNoMoreInteractions(blogPostsDAO);
    }

    @Test
    public void testUpdateBlogPostI18nContent() throws Exception {
        when(blogPostI18nContentsDAO.getByLanguageForPost(anyLong(), anyLong())).thenReturn(null);
        when(blogPostI18nContentsDAO.add(any(BlogPostI18nContent.class))).thenReturn(1l);
        when(blogPostsDAO.get(anyLong())).thenReturn(new BlogPost());

        ResultActions resultActions = mockMvc.perform(post("/blogPosts/updateBlogPostI18nContent")
                .param("postTitle", "postTitle")
                .param("postShortcut", "postShortcut")
                .param("postDescription", "postDescription"));

        resultActions.andExpect(status().isSeeOther())
                .andExpect(model().attribute("mainJavaScript", "/resources-cwsfe-cms/js/cms/blog/SinglePost.js"))
                .andExpect(model().attribute("breadcrumbs", anything()));
        verify(blogPostI18nContentsDAO, times(1)).getByLanguageForPost(anyLong(), anyLong());
        verify(blogPostI18nContentsDAO, times(1)).add(any(BlogPostI18nContent.class));
        verifyNoMoreInteractions(blogPostI18nContentsDAO);
    }

    @Test
    public void testPostCategoriesUpdate() throws Exception {
        doNothing().when(blogPostKeywordsDAO).deleteForPost(anyLong());
        doNothing().when(blogPostKeywordsDAO).add(any(BlogPostKeyword.class));
        when(blogPostKeywordsDAO.listForPost(anyLong())).thenReturn(new ArrayList<>());
        when(blogPostsDAO.get(anyLong())).thenReturn(new BlogPost());

        ResultActions resultActions = mockMvc.perform(post("/postCategoriesUpdate")
                .param("postCategories", "1")
                .param("postCategories", "2")
                .param("postCategories", "3")
                .param("id", "1"));

        resultActions.andExpect(status().isSeeOther())
                .andExpect(model().attribute("mainJavaScript", "/resources-cwsfe-cms/js/cms/blog/SinglePost.js"))
                .andExpect(model().attribute("breadcrumbs", anything()));
        verify(blogPostKeywordsDAO, times(1)).deleteForPost(anyLong());
        verify(blogPostKeywordsDAO, times(3)).add(any(BlogPostKeyword.class));
        verify(blogPostKeywordsDAO, times(1)).listForPost(anyLong());
        verifyNoMoreInteractions(blogPostKeywordsDAO);
    }
}