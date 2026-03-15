# Floaty Chathead (Deprecated)

[![Deprecated](https://img.shields.io/badge/status-deprecated-red.svg)](https://pub.dev/packages/floaty_chatheads)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

> **This package has been deprecated.** Please use [`floaty_chatheads`](https://pub.dev/packages/floaty_chatheads) instead.

---

## Migration

Replace your dependency:

```yaml
# Before
dependencies:
  floaty_chathead: ^3.0.0

# After
dependencies:
  floaty_chatheads: ^1.0.0
```

Then run:

```bash
flutter pub get
```

## What changed?

`floaty_chatheads` is a full rewrite of `floaty_chathead` using a **federated plugin architecture**, bringing:

- **iOS support** alongside Android
- **Type-safe platform channels** via Pigeon (no more manual `MethodChannel` strings)
- **Typed messaging** with `FloatyMessenger<T>` for serialized communication between main app and overlay
- **Lifecycle-aware controller** (`FloatyController`) with `ChangeNotifier` integration
- **Widget-level integration** via `FloatyScope` (InheritedWidget) and `FloatyPermissionGate`
- **Multi-chathead management** (`addChatHead`, `removeChatHead`, `expandChatHead`, `collapseChatHead`)
- **Built-in UI components** (`FloatyMiniPlayer`, `FloatyNotificationCard`)
- **100% test coverage** on handwritten code with 132 tests
- **Testing utilities** (`FakeFloatyPlatform`, `FakeOverlayDataSource`) for easy widget testing

## Links

- [`floaty_chatheads` on pub.dev](https://pub.dev/packages/floaty_chatheads)
- [`floaty_chatheads` repository](https://github.com/Crdzbird/floaty_chatheads)

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
