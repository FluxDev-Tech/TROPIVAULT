// lib/models/user_model.dart
import 'dart:convert';

/// Defined user roles in TropiVault
enum UserRole {
  client,
  farmer,
  rider,
  admin;

  String toValue() => name.toUpperCase();

  static UserRole fromString(String? value) {
    switch (value?.toUpperCase()) {
      case 'FARMER':
        return UserRole.farmer;
      case 'RIDER':
        return UserRole.rider;
      case 'ADMIN':
        return UserRole.admin;
      case 'CLIENT':
      default:
        return UserRole.client;
    }
  }

  String get displayName {
    switch (this) {
      case UserRole.client:
        return 'Client / Business Buyer';
      case UserRole.farmer:
        return 'Verified Farmer Partner';
      case UserRole.rider:
        return 'Climate-Box Rider';
      case UserRole.admin:
        return 'System Administrator';
    }
  }
}

/// Verification status for partner accounts (Farmer & Rider)
enum UserStatus {
  approved,
  pending,
  rejected;

  String toValue() => name.toUpperCase();

  static UserStatus fromString(String? value) {
    switch (value?.toUpperCase()) {
      case 'PENDING':
        return UserStatus.pending;
      case 'REJECTED':
        return UserStatus.rejected;
      case 'APPROVED':
      default:
        return UserStatus.approved;
    }
  }
}

/// Comprehensive User Model for TropiVault
class UserModel {
  final int id;
  final String email;
  final String fullName;
  final UserRole role;
  final UserStatus status;
  final String phone;
  final String address;
  final String? farmName;
  final String? farmLocation;
  final String? vehicleType;
  final String? licenseNumber;
  final String? profileImageUrl;
  final String? token;
  final DateTime createdAt;

  const UserModel({
    required this.id,
    required this.email,
    required this.fullName,
    required this.role,
    this.status = UserStatus.approved,
    this.phone = '',
    this.address = '',
    this.farmName,
    this.farmLocation,
    this.vehicleType,
    this.licenseNumber,
    this.profileImageUrl,
    this.token,
    required this.createdAt,
  });

  // Role Differentiation Helpers
  bool get isClient => role == UserRole.client;
  bool get isFarmer => role == UserRole.farmer;
  bool get isRider => role == UserRole.rider;
  bool get isAdmin => role == UserRole.admin;

  // Status Differentiation Helpers
  bool get isApproved => status == UserStatus.approved;
  bool get isPending => status == UserStatus.pending;
  bool get isRejected => status == UserStatus.rejected;

  /// Checks if user can access authenticated transactional operations
  bool get canTransact => isApproved && (isClient || isFarmer || isRider || isAdmin);

  UserModel copyWith({
    int? id,
    String? email,
    String? fullName,
    UserRole? role,
    UserStatus? status,
    String? phone,
    String? address,
    String? farmName,
    String? farmLocation,
    String? vehicleType,
    String? licenseNumber,
    String? profileImageUrl,
    String? token,
    DateTime? createdAt,
  }) {
    return UserModel(
      id: id ?? this.id,
      email: email ?? this.email,
      fullName: fullName ?? this.fullName,
      role: role ?? this.role,
      status: status ?? this.status,
      phone: phone ?? this.phone,
      address: address ?? this.address,
      farmName: farmName ?? this.farmName,
      farmLocation: farmLocation ?? this.farmLocation,
      vehicleType: vehicleType ?? this.vehicleType,
      licenseNumber: licenseNumber ?? this.licenseNumber,
      profileImageUrl: profileImageUrl ?? this.profileImageUrl,
      token: token ?? this.token,
      createdAt: createdAt ?? this.createdAt,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'email': email,
      'fullName': fullName,
      'role': role.toValue(),
      'status': status.toValue(),
      'phone': phone,
      'address': address,
      if (farmName != null) 'farmName': farmName,
      if (farmLocation != null) 'farmLocation': farmLocation,
      if (vehicleType != null) 'vehicleType': vehicleType,
      if (licenseNumber != null) 'licenseNumber': licenseNumber,
      if (profileImageUrl != null) 'profileImageUrl': profileImageUrl,
      if (token != null) 'token': token,
      'createdAt': createdAt.toIso8601String(),
    };
  }

  factory UserModel.fromMap(Map<String, dynamic> map) {
    return UserModel(
      id: map['id'] is int ? map['id'] : int.tryParse(map['id']?.toString() ?? '0') ?? 0,
      email: map['email'] ?? '',
      fullName: map['fullName'] ?? map['name'] ?? '',
      role: UserRole.fromString(map['role']),
      status: UserStatus.fromString(map['status']),
      phone: map['phone'] ?? '',
      address: map['address'] ?? '',
      farmName: map['farmName'],
      farmLocation: map['farmLocation'],
      vehicleType: map['vehicleType'],
      licenseNumber: map['licenseNumber'],
      profileImageUrl: map['profileImageUrl'],
      token: map['token'],
      createdAt: map['createdAt'] != null
          ? DateTime.tryParse(map['createdAt']) ?? DateTime.now()
          : DateTime.now(),
    );
  }

  String toJson() => json.encode(toMap());

  factory UserModel.fromJson(String source) => UserModel.fromMap(json.decode(source));

  @override
  String toString() {
    return 'UserModel(id: $id, email: $email, role: $role, status: $status, name: $fullName)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is UserModel && other.id == id && other.email == email && other.role == role;
  }

  @override
  int get hashCode => id.hashCode ^ email.hashCode ^ role.hashCode;
}
