package com.example.account

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class AccountService constructor(val accountRepository: AccountRepository) {

    @Transactional(readOnly = true)
    open fun findAll(): MutableList<Account> {
        return accountRepository.findAll()
    }

    open fun save(accountForm: AccountForm) {
        accountRepository.save(Account(accountForm.name))
    }
}
