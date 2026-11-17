# Laboratory Work No. 2: Structural Design Patterns

## Overview
This laboratory work extends the e-commerce checkout and order-processing architecture from **Lab 0** (SOLID principles) and **Lab 1** (Creational patterns) by implementing **3 Structural Design Patterns** in Kotlin:

1. **Adapter Pattern**: Adapts Java's built-in `java.net.http.HttpClient` and an external public REST exchange rate API into the domain's `CurrencyConverter` target interface, allowing orders to be quoted in base currency (MDL) and paid in foreign currencies (EUR, USD, etc.).
2. **Decorator Pattern**: Dynamically composes cross-cutting concerns around payment processing without subclass explosion, including security and AML screening (`FraudDetectionPaymentDecorator`) and latency audit tracking (`AuditLoggingPaymentDecorator`), which can be stacked in any arbitrary order.
3. **Facade Pattern**: Exposes a clean, high-level interface (`StoreCheckoutFacade`) that coordinates the entire multi-step subsystem: Order Building (Builder), Channel Selection (Abstract Factory), Currency Conversion (Adapter), and Payment Processing (Decorator pipeline).

### Pattern Interaction
Client code interacts with high-level abstractions through an execution path linking all patterns:
```
Client -> StoreCheckoutFacade (Facade)
       -> HttpCurrencyExchangeAdapter (Adapter) -> HttpClient (External API)
       -> FraudDetectionPaymentDecorator (Decorator)
       -> AuditLoggingPaymentDecorator (Decorator)
       -> StandardPaymentProcessor (Component)
```

---

## Project Structure

```
lab2/
├── build.sh                                     # Automated compilation script
├── run.sh                                       # CLI demo runner script
├── test.sh                                      # Automated unit test suite runner
├── README.md                                    # Project documentation
├── REPORT.md                                    # Detailed verification report
├── lab2.iml                                     # IntelliJ IDEA module descriptor
└── src/
    ├── main/kotlin/com/tmps/lab2/
    │   ├── client/
    │   │   └── Main.kt                          # Runnable demonstration entry point
    │   ├── domain/                              # Reused domain entities from Lab 0 & Lab 1
    │   │   ├── CheckoutConfig.kt                # Singleton store configuration
    │   │   ├── CheckoutFactories.kt             # Abstract Factory implementations
    │   │   ├── Contracts.kt                     # Pricing & Printer contracts
    │   │   ├── Models.kt                        # Order and OrderItem entities
    │   │   ├── OrderBuilder.kt                  # Builder pattern for Order
    │   │   ├── PricingStrategies.kt             # Pricing strategies
    │   │   └── ReceiptPrinters.kt               # Retail & Wholesale receipt printers
    │   └── patterns/
    │       ├── adapter/
    │       │   ├── CurrencyConverter.kt         # Target interface
    │       │   └── HttpCurrencyExchangeAdapter.kt # Adapter for external HTTP FX API
    │       ├── decorator/
    │       │   ├── PaymentProcessor.kt          # Common Component interface
    │       │   ├── StandardPaymentProcessor.kt  # Concrete Component
    │       │   ├── PaymentDecorator.kt          # Base Decorator
    │       │   ├── AuditLoggingPaymentDecorator.kt # Concrete Decorator (audit)
    │       │   └── FraudDetectionPaymentDecorator.kt # Concrete Decorator (fraud)
    │       └── facade/
    │           ├── CheckoutFacade.kt            # Facade abstraction
    │           ├── CheckoutRequest.kt           # Input DTOs
    │           ├── CheckoutResponse.kt          # Output DTOs
    │           └── StoreCheckoutFacade.kt       # Facade orchestrator implementation
    └── test/kotlin/com/tmps/lab2/
        ├── AdapterPatternTest.kt                # Unit tests for Adapter pattern
        ├── DecoratorPatternTest.kt              # Unit tests for Decorator pattern
        ├── FacadePatternTest.kt                 # Unit tests for Facade pattern
        └── TestRunner.kt                        # Single-command automated test runner
```

---

## Prerequisites
- Java Development Kit (JDK 17 or newer)
- Kotlin Compiler (`kotlinc` 1.9+ / 2.0+)

---

## Build and Run Instructions

### 1. Build Project
```bash
./build.sh
```
Compiles all main and test Kotlin sources into `out/production/lab2.jar`.

### 2. Run Interactive / Scenario Demo
```bash
./run.sh
```
Executes the CLI scenario demonstration showcasing all 4 transaction scenarios:
- Standard Retail checkout in local currency (MDL).
- Retail checkout with currency conversion to EUR via the external HTTP adapter.
- Wholesale B2B checkout with bulk volume discount and conversion to USD.
- Security interception via Decorator blocking high-risk transactions.

### 3. Run Unit Tests (One-Command)
```bash
./test.sh
```
Executes the automated test suite testing the key invariant properties of each structural pattern (11 tests total, all passing).
