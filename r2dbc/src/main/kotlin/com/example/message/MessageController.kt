package com.example.message

import com.example.account.Account
import com.example.account.AccountRepository
import com.example.account.AccountService
import com.example.formatDateAgo
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.result.view.Rendering
import java.time.LocalDateTime
import javax.validation.Valid

@Controller
@RequestMapping("/message")
class MessageController(private val messageService: MessageService, private val accountService: AccountService) {

    @ModelAttribute("messages")
    fun messages() = messageService.findAll()
        .flatMap {
            accountService.findById(it.accountId)
                .map { account ->
                    it.toDto(account)
                }
        }

    @ModelAttribute
    fun account(@AuthenticationPrincipal account: Account) = account

    @GetMapping
    fun findAll() = Rendering.view("message")

    @PostMapping
    fun save(@Valid @ModelAttribute messageForm: MessageForm, bindingResult: BindingResult, account: Account): Rendering {

        if (bindingResult.hasErrors()) {

            return Rendering.view("message").build()

        }

        return Rendering.redirectTo("/message")
            .modelAttribute("message", messageService.save(messageForm.toMessage(account)))
            .build()

    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: String): Rendering {

        return Rendering.redirectTo("/message")
            .modelAttribute("message", messageService.delete(id))
            .build()


    }
}

fun MessageForm.toMessage(account: Account) = Message(this.message, account.id!!)

fun Message.toDto(account: Account) = MessageDto(this.message, account, this.regDate.formatDateAgo(), id)

data class MessageDto(

    val message: String,

    val account: Account,

    val regDate: String,

    val id: Long?
)