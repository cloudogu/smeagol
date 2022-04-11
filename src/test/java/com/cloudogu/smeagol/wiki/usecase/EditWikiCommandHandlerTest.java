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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EditWikiCommandHandlerTest {

    @Mock
    private WikiRepository wikiRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private EditWikiCommandHandler commandHandler;

    @Test
    public void testEdit() {
        when(accountService.get()).thenReturn(AccountTestData.TRILLIAN);

        WikiId id = new WikiId("123", "master");
        WikiSettings newSettings = new WikiSettings(DisplayName.valueOf("displayName"),
            Path.valueOf("docs"), Path.valueOf("home"));

        Message message = Message.valueOf("hitchhiker is awesome");

        commandHandler.handle(new EditWikiCommand(id, message, newSettings));

        ArgumentCaptor<Commit> captor = ArgumentCaptor.forClass(Commit.class);
        verify(wikiRepository).save(eq(id), captor.capture(), eq(newSettings));

        assertEquals(message, captor.getValue().getMessage());
    }

}
