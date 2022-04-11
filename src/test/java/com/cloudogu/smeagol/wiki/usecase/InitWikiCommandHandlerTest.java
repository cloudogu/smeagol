package com.cloudogu.smeagol.wiki.usecase;

import com.cloudogu.smeagol.AccountService;
import com.cloudogu.smeagol.AccountTestData;
import com.cloudogu.smeagol.wiki.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InitWikiCommandHandlerTest {

    @Mock
    private WikiRepository wikiRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private InitWikiCommandHandler commandHandler;

    @Test
    public void testInit() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        WikiSettings newSettings = new WikiSettings(DisplayName.valueOf("displayName"),
            Path.valueOf("docs"), Path.valueOf("home"));
        Message message = Message.valueOf("hitchhiker is awesome");
        Wiki expectedWiki = new Wiki(null, null, null, null, null, null);
        doReturn(expectedWiki).when(wikiRepository).init(eq(id), any(), eq(newSettings));

        Wiki actualWiki = commandHandler.handle(new InitWikiCommand(id, message, newSettings));

        ArgumentCaptor<Commit> captor = ArgumentCaptor.forClass(Commit.class);
        verify(wikiRepository).init(eq(id), captor.capture(), eq(newSettings));
        assertEquals(message, captor.getValue().getMessage());
        assertEquals(expectedWiki, actualWiki);
    }
}
