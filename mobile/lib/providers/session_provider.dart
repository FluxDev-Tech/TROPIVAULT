// lib/providers/session_provider.dart
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/user_model.dart';

/// Immutable representation of the current authentication and session state
class SessionState {
  final UserModel? user;
  final bool isLoading;
  final String? errorMessage;
  final String? pendingNotice;

  const SessionState({
    this.user,
    this.isLoading = false,
    this.errorMessage,
    this.pendingNotice,
  });

  // State derivation getters
  bool get isAuthenticated => user != null && user!.isApproved;
  bool get isPendingApproval => user != null && user!.isPending;
  bool get hasError => errorMessage != null;

  // Direct role differentiation accessors
  UserRole? get role => user?.role;
  bool get isClient => user?.isClient ?? false;
  bool get isFarmer => user?.isFarmer ?? false;
  bool get isRider => user?.isRider ?? false;
  bool get isAdmin => user?.isAdmin ?? false;

  SessionState copyWith({
    UserModel? user,
    bool? isLoading,
    String? errorMessage,
    String? pendingNotice,
    bool clearUser = false,
    bool clearError = false,
    bool clearNotice = false,
  }) {
    return SessionState(
      user: clearUser ? null : (user ?? this.user),
      isLoading: isLoading ?? this.isLoading,
      errorMessage: clearError ? null : (errorMessage ?? this.errorMessage),
      pendingNotice: clearNotice ? null : (pendingNotice ?? this.pendingNotice),
    );
  }

  factory SessionState.initial() => const SessionState();
  factory SessionState.loading() => const SessionState(isLoading: true);
  factory SessionState.unauthenticated() => const SessionState();
}

/// StateNotifier handling TropiVault user session, role dispatch, and authentication
class SessionNotifier extends StateNotifier<SessionState> {
  SessionNotifier() : super(SessionState.initial()) {
    _loadPersistedSession();
  }

  /// Initialize session from secure storage or cache
  Future<void> _loadPersistedSession() async {
    // In production: Read token from FlutterSecureStorage and validate against backend /api/auth/me
    // Default initial guest session allows exploring public marketplace without blocking
    state = SessionState.unauthenticated();
  }

  /// Authenticate with email & password
  Future<bool> login(String email, String password) async {
    state = state.copyWith(isLoading: true, clearError: true, clearNotice: true);

    try {
      // Simulation / REST API integration: POST /api/auth/login
      await Future.delayed(const Duration(milliseconds: 600));

      final cleanEmail = email.trim().toLowerCase();

      // Check role mapping
      UserModel matchedUser;
      if (cleanEmail.contains('admin')) {
        matchedUser = UserModel(
          id: 1,
          email: email,
          fullName: 'Maria Santos (TropiVault Admin)',
          role: UserRole.admin,
          status: UserStatus.approved,
          phone: '0917-888-0001',
          address: 'TropiVault HQ, BGC, Taguig',
          token: 'jwt_mock_token_admin_7781',
          createdAt: DateTime.now().subtract(const Duration(days: 30)),
        );
      } else if (cleanEmail.contains('farmer')) {
        matchedUser = UserModel(
          id: 2,
          email: email,
          fullName: 'Ramon Valderrama',
          role: UserRole.farmer,
          status: UserStatus.approved,
          farmName: 'Guimaras Heritage Orchards',
          farmLocation: 'Jordan, Guimaras Island',
          phone: '0917-555-1234',
          token: 'jwt_mock_token_farmer_8812',
          createdAt: DateTime.now().subtract(const Duration(days: 20)),
        );
      } else if (cleanEmail.contains('rider')) {
        matchedUser = UserModel(
          id: 3,
          email: email,
          fullName: 'Jun Morales',
          role: UserRole.rider,
          status: UserStatus.approved,
          vehicleType: 'Motorcycle (Insulated Cold-Box)',
          licenseNumber: 'N02-18-994321',
          phone: '0919-444-9876',
          token: 'jwt_mock_token_rider_4490',
          createdAt: DateTime.now().subtract(const Duration(days: 15)),
        );
      } else {
        matchedUser = UserModel(
          id: 4,
          email: email,
          fullName: 'Sofia Dela Cruz',
          role: UserRole.client,
          status: UserStatus.approved,
          phone: '0917-999-2345',
          address: 'Unit 14C, Bellagio Tower 2, BGC, Taguig City',
          token: 'jwt_mock_token_client_1120',
          createdAt: DateTime.now().subtract(const Duration(days: 10)),
        );
      }

      if (matchedUser.isPending) {
        state = state.copyWith(
          user: matchedUser,
          isLoading: false,
          pendingNotice: 'Your ${matchedUser.role.displayName} application is awaiting administrator verification.',
        );
        return false;
      }

      state = state.copyWith(
        user: matchedUser,
        isLoading: false,
        clearError: true,
      );
      return true;
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        errorMessage: 'Invalid credentials. Please verify your email and password.',
      );
      return false;
    }
  }

  /// Sign in with Google Auth Token
  Future<bool> loginWithGoogle(String idToken) async {
    state = state.copyWith(isLoading: true, clearError: true);
    try {
      // In production: Send idToken to POST /api/auth/google for verification
      await Future.delayed(const Duration(milliseconds: 500));

      final googleUser = UserModel(
        id: 10,
        email: 'google.user@example.com',
        fullName: 'Google Authenticated User',
        role: UserRole.client,
        status: UserStatus.approved,
        token: 'jwt_google_verified_token',
        createdAt: DateTime.now(),
      );

      state = state.copyWith(user: googleUser, isLoading: false);
      return true;
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        errorMessage: 'Google authentication failed. Please try again.',
      );
      return false;
    }
  }

  /// Register a Client / Business Account (Instant Approval)
  Future<bool> registerClient({
    required String fullName,
    required String email,
    required String phone,
    required String password,
    String address = '',
  }) async {
    state = state.copyWith(isLoading: true, clearError: true);
    try {
      await Future.delayed(const Duration(milliseconds: 700));

      final newUser = UserModel(
        id: DateTime.now().millisecondsSinceEpoch % 100000,
        email: email.trim(),
        fullName: fullName.trim(),
        role: UserRole.client,
        status: UserStatus.approved,
        phone: phone.trim(),
        address: address.trim(),
        token: 'jwt_client_token_${DateTime.now().millisecondsSinceEpoch}',
        createdAt: DateTime.now(),
      );

      state = state.copyWith(user: newUser, isLoading: false);
      return true;
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
      return false;
    }
  }

  /// Register a Farmer Partner (Placed in Pending status for Admin review)
  Future<bool> registerFarmer({
    required String fullName,
    required String farmName,
    required String farmLocation,
    required String email,
    required String phone,
    required String password,
  }) async {
    state = state.copyWith(isLoading: true, clearError: true);
    try {
      await Future.delayed(const Duration(milliseconds: 700));

      final newFarmer = UserModel(
        id: DateTime.now().millisecondsSinceEpoch % 100000,
        email: email.trim(),
        fullName: fullName.trim(),
        role: UserRole.farmer,
        status: UserStatus.pending, // Pending review per specification
        farmName: farmName.trim(),
        farmLocation: farmLocation.trim(),
        phone: phone.trim(),
        createdAt: DateTime.now(),
      );

      state = state.copyWith(
        user: newFarmer,
        isLoading: false,
        pendingNotice: 'Farmer partner application submitted. A TropiVault admin will review your orchard credentials before activation.',
      );
      return true;
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
      return false;
    }
  }

  /// Register a Climate-Box Rider Partner (Placed in Pending status)
  Future<bool> registerRider({
    required String fullName,
    required String email,
    required String phone,
    required String address,
    required String vehicleType,
    required String licenseNumber,
    required String password,
  }) async {
    state = state.copyWith(isLoading: true, clearError: true);
    try {
      await Future.delayed(const Duration(milliseconds: 700));

      final newRider = UserModel(
        id: DateTime.now().millisecondsSinceEpoch % 100000,
        email: email.trim(),
        fullName: fullName.trim(),
        role: UserRole.rider,
        status: UserStatus.pending, // Pending review
        phone: phone.trim(),
        address: address.trim(),
        vehicleType: vehicleType.trim(),
        licenseNumber: licenseNumber.trim(),
        createdAt: DateTime.now(),
      );

      state = state.copyWith(
        user: newRider,
        isLoading: false,
        pendingNotice: 'Rider registration submitted. An administrator will verify your thermal transport equipment shortly.',
      );
      return true;
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
      return false;
    }
  }

  /// Instant role switcher for fast evaluation across roles
  void quickSwitchRole(UserRole targetRole) {
    UserModel mockUser;
    switch (targetRole) {
      case UserRole.admin:
        mockUser = UserModel(
          id: 1,
          email: 'admin@tropivault.com',
          fullName: 'Maria Santos (Admin)',
          role: UserRole.admin,
          status: UserStatus.approved,
          phone: '0917-888-0001',
          createdAt: DateTime.now(),
        );
        break;
      case UserRole.farmer:
        mockUser = UserModel(
          id: 2,
          email: 'farmer.ramon@guimarasfarms.com',
          fullName: 'Ramon Valderrama',
          role: UserRole.farmer,
          status: UserStatus.approved,
          farmName: 'Guimaras Heritage Orchards',
          farmLocation: 'Jordan, Guimaras',
          phone: '0917-555-1234',
          createdAt: DateTime.now(),
        );
        break;
      case UserRole.rider:
        mockUser = UserModel(
          id: 3,
          email: 'rider.jun@tropivault.com',
          fullName: 'Jun Morales',
          role: UserRole.rider,
          status: UserStatus.approved,
          vehicleType: 'Motorcycle (Insulated Vault Box)',
          phone: '0919-444-9876',
          createdAt: DateTime.now(),
        );
        break;
      case UserRole.client:
      default:
        mockUser = UserModel(
          id: 4,
          email: 'client.sofia@freshbites.com',
          fullName: 'Sofia Dela Cruz',
          role: UserRole.client,
          status: UserStatus.approved,
          phone: '0917-999-2345',
          address: 'BGC, Taguig City',
          createdAt: DateTime.now(),
        );
        break;
    }

    state = SessionState(user: mockUser, isLoading: false);
  }

  /// Terminate session and return to guest / unauthenticated status
  void logout() {
    state = SessionState.unauthenticated();
  }

  /// Clear any active error banner
  void clearError() {
    state = state.copyWith(clearError: true);
  }
}

// ---------------------------------------------------------------------------
// RIVERPOD PROVIDER DECLARATIONS
// ---------------------------------------------------------------------------

/// Primary session provider exposing the active SessionState
final sessionProvider = StateNotifierProvider<SessionNotifier, SessionState>((ref) {
  return SessionNotifier();
});

/// Exposes the active UserModel or null if guest
final currentUserProvider = Provider<UserModel?>((ref) {
  return ref.watch(sessionProvider).user;
});

/// Exposes the active UserRole enum (Client, Farmer, Rider, Admin)
final userRoleProvider = Provider<UserRole?>((ref) {
  return ref.watch(sessionProvider).role;
});

/// Boolean provider indicating whether the user is successfully logged in and approved
final isAuthenticatedProvider = Provider<bool>((ref) {
  return ref.watch(sessionProvider).isAuthenticated;
});

/// True if active session is an approved Client / Business buyer
final isClientProvider = Provider<bool>((ref) {
  return ref.watch(sessionProvider).isClient;
});

/// True if active session is an approved Farmer partner
final isFarmerProvider = Provider<bool>((ref) {
  return ref.watch(sessionProvider).isFarmer;
});

/// True if active session is an approved Rider partner
final isRiderProvider = Provider<bool>((ref) {
  return ref.watch(sessionProvider).isRider;
});

/// True if active session is an Administrator
final isAdminProvider = Provider<bool>((ref) {
  return ref.watch(sessionProvider).isAdmin;
});

/// True if the user registered a Farmer or Rider account that is awaiting Admin verification
final isPendingApprovalProvider = Provider<bool>((ref) {
  return ref.watch(sessionProvider).isPendingApproval;
});
