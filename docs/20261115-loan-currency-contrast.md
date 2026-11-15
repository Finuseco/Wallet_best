# Loan balances, currency presentation and review — Wallet 12.7

The screenshot's invisible values came from shared RecapRow defaults (white values) on a light background. Loan pages now use their own contrasting two-column details, an explicit light offer/confirmation surface, purple selections, a prominent selected currency and a visible wallet balance. `offer.balance` is the user's wallet balance from the Web API, not a maximum loan amount; the misleading maximum label was removed.

Loan monetary displays use French grouping and explicit currency codes throughout offers, active loans, history and confirmation. CDF omits trailing decimal zeros but retains actual fractional installments; USD/EUR retain two decimals. No exchange conversion, multiplication or zero padding changes monetary values. For example, 50 CDF remains 50 CDF; 50,000 CDF displays as 50 000 CDF. French comma/space input is normalized for loan applications.

Missing/mismatched offer currency no longer silently defaults to USD. Android refuses mismatched offer/quote currencies. Switching currencies clears the amount and invalidates a previous review. Submission checks the accepted quote's principal, duration and currency against the current request. The backend still provides and validates the contractual amounts and pricing.

Version 12.7/code 127. Pure Kotlin checks cover CDF zero/grouping/fractions, USD/EUR decimals, percentages and French amount parsing. No production loan or balance was modified.
