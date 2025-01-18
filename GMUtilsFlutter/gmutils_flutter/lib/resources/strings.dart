import 'package:flutter/material.dart';

import '../zgmutils/gm_main.dart';

class Strings {
  bool get en => App.isEnglish;

  Strings(BuildContext? context);

  String get appName => 'مدارس التعليم ثنائي اللغة';

  String get please_wait => en ? 'Please wait...' : 'يرجى الإنتظار...';

  String get login => en ? 'Login' : 'تسجيل الدخول';

  String get userName_ => en ? 'User name:' : 'إسم المستخدم:';

  String get password_ => en ? 'Password:' : 'كلمة المرور:';

  String get enter_password_at_least => en
      ? 'Enter the password (at least 6-characters)'
      : 'أدخل كلمة المرور (على الأقل ٦ خانات)';

  String get error => en ? 'Error' : 'خطأ';

  String get reset => en ? 'Reset' : 'إستعادة';

  String get message => en ? 'Message' : 'رسالة';

  String get ok => en ? 'OK' : 'حسنا';

  String get dismiss => en ? 'Dismiss' : 'إغلاق';

  String get retry => en ? 'Retry' : 'إعـادة';

  String get check_following_error =>
      en ? 'Check the following errors:' : 'راجع الأخطاء التالية:';

  String get please_check_your_email => en
      ? 'Please check your Email inbox'
      : 'يرجى مراجعة صندوق البريد الإلكتروني الخاص بك';

  String get cancel => en ? 'Cancel' : 'إلغاء';

  String get status => en ? 'Status' : 'الحالة';

  String get loading => en ? 'Loading...' : 'تحميل...';

  String get all => en ? 'All' : 'الكل';

  String get sorting => en ? 'Sorting' : 'ترتيب';

  String get defaults => en ? 'Default' : 'الإفتراضي';

  String get refresh => en ? 'Refresh' : 'تحديث';

  String get description => en ? 'Description' : 'الوصف';

  String get rating_ => en ? 'Rating:' : 'التقييم:';

  String get rating => en ? 'Rating' : 'التقييم';

  String get messages_ => en ? 'Messages:' : 'الرسائل:';

  String get there_are_no_messages =>
      en ? 'There are no messages.' : 'لا توجد رسائل.';

  String get there_are_no_selections_till_now =>
      en ? 'There are no selections till now.' : 'لا توجد إختيارات حتى الآن.';

  String get sendMessage => en ? 'Send Message' : 'أرسل الرسالة';

  String get select_one => en ? 'Select one' : 'إختار أحدهم';

  String get notifications => en ? 'Notifications' : 'التنبيهات';

  String get markAsRead => en ? 'Mark as read' : 'تحديد الكل كمقروء';

  String get logout => en ? 'Logout' : 'تسجيل الخروج';

  String get confirmation => en ? 'Confirmation' : 'تأكيد';

  String get are_you_sure_of_logout =>
      en ? 'Are you sure of logout?' : 'هل تريد بالفعل تسجيل الخروج؟';

  String get alert => en ? 'Alert' : 'تنبيه';

  String get a_new_version_has_been_released_please_update => en
      ? 'A new version has been released, please update.'
      : 'إصدار جديد تم إطلاقه، يرجى التحديث.';

  String get update => en ? 'Update' : 'تحديث';

  String get notes_ => en ? 'Notes:' : 'ملاحظات:';

  String get skip => en ? 'Skip' : 'تجاوز';

  String get to => en ? 'to' : 'إلى';

  String get To => en ? 'To' : 'إلى';

  String get Continue => en ? 'Continue' : 'إستمرار';

  String get send => en ? 'Send' : 'إرسال';

  String get previous => en ? 'Previous' : 'السابق';
  String get next => en ? 'Next' : 'التالي';

  String get pounds => en ? 'pounds' : 'جنيه';

  String get broadcast => en ? 'Broadcast' : 'رسالة عامة';

  String get privileges => en ? 'Privileges' : 'الصلاحيات';

  String get you_dont_have_the_privilege_of =>
      en ? 'You don\'t have the privilege of' : 'ليس لديك صلاحية';

  String get messages => en ? 'Messages' : 'الرسائل';

  String get contact_us => en ? 'Contact us' : 'اتصل بنا';

  String get enterYourName => en ? 'Enter your name' : 'أدخل الإسم';

  String get yourName_ => en ? 'You name:' : 'الإسم:';

  String get emailAddress_ => en ? 'Email Address:' : 'البريد الإلكتروني:';

  String get enterYourEmailAddress =>
      en ? 'Enter your Email address' : 'أدخل بريدك الإلكتروني';

  String get phone_ => en ? 'Phone:' : 'الهاتف';

  String get enterYourPhone =>
      en ? 'Enter your phone number' : 'أدخل رقم هاتفك';

  String get your_email_or_password_is_wrong => en
      ? 'Your Email or password is wrong.'
      : 'البريد الإلكتروني أو كلمة المرور خاطئة.';

  String get userName => en ? 'User Name' : 'إسم المستخدم';

  String get enterYourUserName =>
      en ? 'Enter your User Name' : 'أدخل إسم المستخدم';

  String get enterValidUserName =>
      en ? 'Enter a valid User Name' : 'أدخل إسم المستخدم صحيح';

  String get password => en ? 'Password' : 'كلمة المرور';

  String get newPassword => en ? 'New Password' : 'كلمة المرور الجديدة';

  String get confirmPassword => en ? 'Confirm Password' : 'تأكيد كلمة المرور';

  String get confirmPassword_ =>
      en ? 'Confirm Password:' : 'تأكيد كلمة المرور:';

  String get enterYourPassword =>
      en ? 'Enter your Password' : 'أدخل كلمة المرور';

  String get enterNewPassword =>
      en ? 'Enter new Password' : 'أدخل كلمة المرور الجديدة';

  String get confirmNewPassword =>
      en ? 'Confirm new Password' : 'قم بتأكيد كلمة المرور الجديدة';

  String get enterThePassword => en ? 'Enter the Password' : 'أدخل كلمة المرور';

  String get enterValidPassword => en
      ? 'Enter a valid Password (6-chars at least & no spaces allowed)'
      : 'أدخل كلمة مرور صحيحة (٦-أحرف على الأقل & المسافات غير مسموحة)';

  String get login_errors => en ? 'Login Errors' : 'أخطاء ببيانات الدخول';

  String get errors => en ? 'Errors' : 'أخطاء بالبيانات';

  String get message_ => en ? 'Message:' : 'رسالة:';

  String get forgotPassword => en ? 'Forgot Password?' : 'نسيت كلمة المرور؟';

  String get restorePassword => en ? 'Restore Password' : 'إستعادة كلمة المرور';

  String get restore => en ? 'Restore' : 'إستعادة';

  String get emailAddress => en ? 'E-mail Address' : 'عنوان البريد الإلكتروني';

  String get resetEmailAddress =>
      en ? 'Reset E-mail Address' : 'عنوان البريد الإلكتروني للإستعادة';

  String get an_email_has_been_sent_to_ =>
      en ? 'An Email has been sent to' : 'تم إرسال بريد إلكتروني إلى';

  String get copy_reset_code_for_continue => en
      ? 'Copy reset code then insert in dedicated box'
      : 'قم بنسخ الكود وإدخاله في المربع المخصص';

  String get follow_the_link_to_create_new_password => en
      ? 'Follow the link to create a new password.'
      : 'اتبع الرابط المرسل لتعيين كلمة مرور جديدة.';

  String get create_new_account =>
      en ? 'Create New Account' : 'إنشاء حساب جديد';

  String get register_account => en ? 'Register Account' : 'تسجيل حساب';

  String get register => en ? 'Register' : 'تسجيل';

  String get registration => en ? 'Registration' : 'التسجيل';

  String get creatingAccountProfile =>
      en ? 'Creating Account Profile' : 'إنشاء ملف شخصي';

  String get first_name => en ? 'First Name' : 'الإسم الأول';

  String get last_name => en ? 'Last Name' : 'الإسم الأخير';

  String get profession => en ? 'Profession' : 'المهنة';

  String get photo => en ? 'Photo' : 'الصورة';

  String get photo_optional => en ? 'Photo: (Optional)' : 'الصورة: (إختياري)';

  String get profilePhoto => en ? 'Profile photo' : 'صورة الملف الشخصي';

  String get gender => en ? 'Gender' : 'الجنس';

  String get gender_optional => en ? 'Gender (Optional):' : 'النوع (اختياري):';

  String get male => en ? 'Male' : 'ذكر';

  String get female => en ? 'Female' : 'أنثى';

  String get birthday => en ? 'Birthday' : 'تاريخ الميلاد';

  String get specifyBirthday =>
      en ? 'Specify your birthday' : 'حدد تاريخ ميلادك';

  String get visibility_ => en ? 'Visibility:' : 'يظهر إلى:';

  String get birthday_visibility =>
      en ? 'Birthday Visibility' : 'الإطلاع على تاريخ الميلاد';

  String get select_birthday_visibility =>
      en ? 'Select Birthday Visibility' : 'حدد خصوصية تاريخ ميلادك';

  String get mobile => en ? 'Mobile' : 'الموبايل';

  String get mobileNumber_ => en ? 'Mobile number:' : 'رقم الموبايل:';

  String get mobileNumber => en ? 'Mobile number' : 'رقم الموبايل';

  String get phoneNumber => en ? 'Phone number' : 'رقم الهاتف';

  String get mobile_visibility =>
      en ? 'Mobile Visibility' : 'الإطلاع على الموبايل';

  String get select_mobile_visibility =>
      en ? 'Select Mobile Visibility' : 'حدد خصوصية رقم الموبايل';

  String get email => en ? 'Email' : 'البريد الإلكتروني';

  String get email_visibility =>
      en ? 'Email Visibility' : 'الإطلاع على البريد الإلكتروني';

  String get select_email_visibility =>
      en ? 'Select Email Visibility' : 'حدد خصوصية البريد الإلكتروني';

  String get all_fields_here_are_mandatory =>
      en ? 'All fields here are mandatory' : 'كل الحقول هنا مطلوبة';

  String get all_fields_here_are_optional =>
      en ? 'All fields here are optional' : 'كل الحقول هنا إختيارية';

  String get next_fields_are_optional =>
      en ? 'Next fields are optional' : 'الحقول التالية إختيارية';

  String get next_fields => en ? 'Next fields' : 'الحقول التالية';

  String get photoUrl => en ? 'Photo link' : 'رابط الصورة';

  String get enterValidName => en ? 'Enter a real Name' : 'أدخل إسم حقيقي';

  String get removeExtraSpaces =>
      en ? 'Remove extra spaces' : 'إحذف المسافات المتكررة';

  String get validation_failed => en ? 'Validation Failed' : 'التحقق غير ناجح';

  String get confirmationPasswordIsNotMatchWithPassword => en
      ? 'Confirmation Password is\'t match with main password'
      : 'كلمة المرور التأكيدية لا تنطبق مع كلة المرور الأساسية';

  String get credentials => en ? 'Credentials' : 'بيانات الدخول';

  String get resetCode => en ? 'Reset Code' : 'رمز إعادة الضبط';

  String get insertResetCodeFromEmail => en
      ? 'Enter the received code on your Email'
      : 'أدخل الكود المرسل لبريدك الإلكترني';

  String get verify => en ? 'Verify' : 'تحقق';

  String get save => en ? 'Save' : 'حفظ';

  String get call_us => en ? 'Call us' : 'اتصل بنا';

  String get theres_no_data => en ? 'There\'s no data.' : 'لا توجد بيانات.';

  String get back => en ? 'Back' : 'رجوع';

  String get from_ => en ? 'From:' : 'من:';

  String get min_age => en ? 'Min Age' : 'أصغر سن';

  String get max_age => en ? 'Max Age' : 'أكبر سن';

  String get none => en ? 'None' : 'لا شيء';

  String get all2 => en ? 'All' : 'الجميع';

  String get everyone => en ? 'Everyone' : 'الجميع';

  String get name_ => en ? 'Name:' : 'الإسم:';

  String get name => en ? 'Name' : 'الإسم';

  String get photos => en ? 'Photos:' : 'الصور:';

  String get landline => en ? 'Landline' : 'رقم الهاتف الأرضي';

  String get landline_ => en ? 'Landline:' : 'رقم الهاتف الأرضي:';

  String get website => en ? 'Website' : 'الموقع الإلكتروني';

  String get website_ => en ? 'Website:' : 'الموقع الإلكتروني:';

  String get facebook => en ? 'Facebook' : 'فيسبوك';

  String get facebook_ => en ? 'Facebook:' : 'فيسبوك:';

  String get twitter => en ? 'Twitter' : 'تويتر';

  String get twitter_ => en ? 'Twitter:' : 'تويتر:';

  String get instagram => en ? 'Instagram' : 'انستجرام';

  String get instagram_ => en ? 'Instagram:' : 'انستجرام:';

  String get name_is_required => en ? 'Name is required.' : 'الإسم مطلوب.';

  String get is_required => en ? 'is required.' : 'مطلوب.';

  get please_review_next_validation_result => en
      ? 'Please review the next validation result:'
      : 'يرجى مراجعة نتيجة التحقق التالية:';

  get please_review_next_fields =>
      en ? 'Please review the next fields:' : 'يرجى مراجعة الحقول التالية:';

  String get login_credential =>
      en ? 'Login Credential' : 'معلومات تسجيل الدخول';

  String get finish => en ? 'Finish' : 'إنهاء';

  String get enterYourMessage => en ? 'Enter your message' : 'أدخل رسالتك';

  String get you_message_has_been_saved_successfully =>
      en ? 'Your message has been sent successfully' : 'تم إسال رسالتك بنجاح.';

  String get search => en ? 'Search' : 'بحث';

  String get confirm => en ? 'Confirm' : 'تأكيد';

  String get category => en ? 'Category' : 'تصنيف';

  String get rated_by_ => en ? 'Rated by:' : 'تم التقييم ب:';

  String get reply => en ? 'Reply' : 'إضافة رد';

  String get reply_ => en ? 'Reply:' : 'الرد:';

  String get share => en ? 'Share' : 'مشاركة';

  String get faqs => en ? 'FAQs' : 'الأسئلة المتكررة';

  String get approve => en ? 'Approve' : 'تأكيد';

  String get not_approve => en ? 'Not approve' : 'غير مؤكد';

  String get approvement => en ? 'Approvement' : 'الموافقة';

  String get warning => en ? 'Warning' : 'تحذير';

  String get are_you_sure => en ? 'Are you sure' : 'هل متأكد';

  String get are_you_sure_you_want_to =>
      en ? 'Are you sure you want to' : 'هل متأكد من';

  String get open => en ? 'Open' : 'فتح';

  String get you_have_notifications =>
      en ? 'You have new notifications' : 'لديك تنبيهات جديدة';

  String get address => en ? 'Address' : 'العنوان';

  String get who_you_are_looking_for =>
      en ? 'Who you are looking for?' : 'عن من تبحث؟';

  String get what_you_are_looking_for =>
      en ? 'What you are looking for?' : 'عن ماذا تبحث؟';

  String get profession_ => en ? 'Profession:' : 'مهنة:';

  String get select_profession => en ? 'Select Profession:' : 'إختار المهنة';

  String get there_are_no_professions_to_filter_by => en
      ? 'There are no professions to filter by'
      : 'لا توجد مهن للفرز بناء عليها';

  String get downloaded => en ? 'Downloaded' : 'تم تحميل';

  String get confidential => en ? 'Confidential' : 'خصوصي';

  String get this_account_rejected =>
      en ? 'This account has been rejected.' : 'تم رفض هذا الحساب.';

  String get error_occured_check_connection_try_again => en
      ? 'Error occurred, please check the connection then try again'
      : 'حدث خطأ، يرجى التأكد من الإتصال وإعادة المحاولة';

  String get about_me => en ? 'About' : 'عني';

  String get about_him => en ? 'About' : 'عنه';

  String get about_her => en ? 'About' : 'عنها';

  String get edit_profile => en ? 'Edit Profile' : 'تعديل الملف الشخصي';

  String get change_password => en ? 'Change Password' : 'تغيير كلمة المرور';

  String get your_account_has_been_blocked =>
      en ? 'Your account has been blocked.' : 'تم حظر حسابك';

  String get your_account_has_been_closed =>
      en ? 'Your account has been closed.' : 'تم غلق حسابك';

  String get until_ => en ? 'until:' : 'حتى:';

  String get the_reason_ => en ? 'the reason:' : 'السبب:';

  String get remove => en ? 'Remove' : 'حذف';

  String get renew => en ? 'Renew' : 'تجديد';

  String get currentPassword => en ? 'Current Password' : 'كلمة المرور الحالية';

  String get entered_password_doesnt_match_with_your_password => en
      ? 'Entered password doesn\'t match with saved password'
      : 'كلمة المرور المدخلة لا تتطابق مع كلمة المرور المحفوظة.';

  String get current_password_doesnt_match_with_your_password => en
      ? 'Entered current password doesn\'t match with saved password'
      : 'كلمة المرور الحالية المدخلة لا تتطابق مع كلمة المرور المحفوظة.';

  String get confirm_password_doesnt_match_with_new_password => en
      ? 'Confirm password doesn\'t match the new password'
      : 'كلمة المرور التأكيدية لا تتطابق مع كلمة المرور الجديدة.';

  String get payments => en ? 'Payments' : 'المدفوعات';

  String get home => en ? 'Home' : 'الرئيسية';

  String get options_ => en ? 'Options:' : 'الخيارات:';

  String get option => en ? 'Option' : 'خيار';

  String get optional => en ? 'Optional' : 'اختياري';

  String get add_option => en ? 'Add Option (+)' : 'إضافة خيار (+)';

  String get like => en ? 'Like' : 'إعجاب';

  String get dislike => en ? 'Dislike' : 'رفض';

  String get write_here => en ? 'Write here' : 'اكتب هنا';

  String get write_your_request_here =>
      en ? 'Write your request here' : 'اكتب طلبك هنا';

  String get delete => en ? 'Delete' : 'حذف';

  String get disable => en ? 'Disable' : 'تعطيل';

  String get submit => en ? 'Submit' : 'إرسال';

  String get type_here => en ? 'Type here' : 'اكتب هنا';

  String get insert_here => en ? 'Insert here' : 'ضع النص هنا';

  String get accept => en ? 'Accept' : 'موافق';

  String get unblock => en ? 'Unblock' : 'إلغاء الحظر';

  String get block => en ? 'Block' : 'حظر';

  String get block_reason_ => en ? 'Block reason:' : 'سبب الحظر:';

  String get block_reason => en ? 'Block reason' : 'سبب الحظر';

  String get report => en ? 'Report' : 'إبلاغ';

  String get controll_user_privileges =>
      en ? 'Control user privileges' : 'تحكم في امتيازات المستخدم';

  String get privilege_Everything => en ? 'Everything' : 'كل شيء';

  String get privilege_SEND_MESSAGE => en ? 'Send messages' : 'إرسال رسائل';

  String get enter_the_reason_of_block =>
      en ? 'Enter the reason of the block' : 'أدخل سبب الحظر';

  String get mark_all_as_read => en ? 'Mark all as read' : 'تعليم الكل كمقروء';

  String get account_type_ => en ? 'Account type:' : 'نوع الحساب:';

  String get account_type => en ? 'Account type' : 'نوع الحساب';

  String get yes => en ? 'Yes' : 'نعم';

  String get no => en ? 'No' : 'لا';

  String get type_your_message => en ? 'Type your message' : 'اكتب رسالتك';

  String get type_your_message_here =>
      en ? 'Type your message here' : 'اكتب رسالتك هنا';

  String get message_sent_successfully =>
      en ? 'Message sent successfully.' : 'تم إرسال الرسالة.';

  String get minute => en ? 'Minute' : 'دقيقة';

  String get minutes => en ? 'Minutes' : 'دقيقة';

  String get second => en ? 'Second' : 'ثانية';

  String get broadcast_sent => en ? 'Broadcast sent.' : 'تمت الإذاعة';

  String get channel_ => en ? 'Channel:' : 'القناة:';

  String get the_title_ => en ? 'The title:' : 'العنوان:';

  String get the_title => en ? 'The title' : 'العنوان';

  String get title_ => en ? 'Title:' : 'العنوان:';

  String get title => en ? 'Title' : 'العنوان';

  String get change => en ? 'Change' : 'تغيير';

  String get address_ => en ? 'Address:' : 'العنوان:';

  String get addressDescription => en ? 'Address description' : 'وصف العنوان';

  String get contactNumber_ => en ? 'Contact Number:' : 'رقم التواصل:';

  String get verify_mobile => en ? 'Verify Phone Number' : 'تحقق من رقم الهاتف';

  String get verify_emailAddress =>
      en ? 'Verify Email Address' : 'تحقق من عنوان البريد الإلكتروني';

  String get bio => en ? 'Bio' : 'السيرة الذاتية';

  String get otherPhoneNumber => en ? 'Another phone number' : 'رقم هاتف آخر';

  String get otherPhoneNumber_hint =>
      en ? 'Another phone number for contacting' : 'رقم هاتف آخر للتواصل';

  String get emailAddress_hint => en
      ? 'Email address is recommended and will help in restoring password'
      : 'يوصى بإدخال عنوان البريد الإلكتروني؛ فسوف تحتاجة إذا نسيت كلمة المرور.';

  String get about => en ? 'About' : 'عنه';

  String get enterTheAddress => en ? 'Enter the address' : 'أدخل العنوان';

  String get select_gender => en ? 'Select the gender' : 'حدد الجنس';

  String get select_birthday => en ? 'Select birthday' : 'حدد تاريخ الميلاد';

  String get main_phoneNumber =>
      en ? 'Main phone number' : 'رقم الهاتف الأساسي';

  String get delete_account => en ? 'Delete account' : 'حذف الحساب';

  String get the_profile => en ? 'The profile' : 'الملف الشخصي';

  String get the_profiles => en ? 'The profiles' : 'الحسابات';

  String get leaveYourCommentHere =>
      en ? 'Leave your comment here' : 'اترك تعليقك هنا';

  String get delete_this_count => en ? 'delete this account' : 'حذف هذا الحساب';

  String get enter_here => en ? 'Enter here' : 'أدخل هنا';

  String get save_changes => en ? 'Save Changes' : 'حفظ التغييرات';

  String get age => en ? 'Age' : 'العمر';

  String get age_ => en ? 'Age:' : 'العمر:';

  String get approval => en ? 'Approval' : 'التفعيل';

  String get this_account_has_been_blocked_due_to => en
      ? 'This account has been blocked due to:'
      : 'تم حظر هذا الحساب لهذا السبب:';

  String get from => en ? 'From' : 'من';

  String get other => en ? 'Other' : 'أخرى';

  String get add_new => en ? 'Add New' : 'إضافة جديد';

  String get price => en ? 'Price' : 'السعر';

  String get price_ => en ? 'Price:' : 'السعر:';

  String get order_ => en ? 'Order:' : 'الترتيب:';

  String get cost => en ? 'Cost' : 'التكلفة';

  String get remain => en ? 'Remain' : 'الباقي';

  String get loadMore => en ? 'Load more' : 'تحميل المزيد';

  String get close => en ? 'Close' : 'إغلاق';

  String get createDate => en ? 'Create Date' : 'تاريخ الإنشاء';

  String get updateDate => en ? 'Update Date' : 'تاريخ التعديل';

  String get conclusion_ => en ? 'Conclusion:' : 'الإستنتاج:';

  String get cancelReason_ => en ? 'Cancel Reason:' : 'سبب الإلغاء:';

  String get cancelReason => en ? 'Cancel Reason' : 'سبب الإلغاء';

  String get discount => en ? 'Discount' : 'الخصم';

  String get questionAndAnswer_ => en ? 'Question & Answer:' : 'سؤال وجواب:';

  String get letQuestion =>
      en ? 'Let a question if you need' : 'اترك سؤالك عند الحاجة لذلك';

  String get letYourQuestionHere =>
      en ? 'Let your question here' : 'اترك سؤالك هنا';

  String get letYourAnswerHere =>
      en ? 'Let your answe here' : 'اترك إجابتك هنا';

  String get noQuestionYet => en ? 'No Questions Yet' : 'لا توجد أسألة.';

  String get the_question_ => en ? 'The question:' : 'السؤال:';

  String get the_answer_ => en ? 'The answer:' : 'الإجابة:';

  String get no_answers =>
      en ? 'There\'s no answers tell now.' : 'لا توجد إجابة حتى الآن.';

  String get ignore => en ? 'Ignore' : 'تجاهل';

  String get pay => en ? 'Pay' : 'الدفع';

  String get working_hours => en ? 'Working Hours' : 'ساعات العمل';

  String get advertisement => en ? 'Advertisement' : 'إعلان';

  String get news => en ? 'News' : 'خبر';

  String get information => en ? 'Information' : 'معلومة';

  String get expiryTime => en ? 'Expiry Time' : 'تنتهي في';

  String get content => en ? 'Content' : 'المحتوى';

  String get link => en ? 'Link' : 'رابط موقع';

  String get upload => en ? 'Upload' : 'تحميل';

  String get post => en ? 'Post' : 'نشـر';

  String get vacation => en ? 'Vacation' : 'أجازة';

  String get saturday => en ? 'Saturday' : 'السبت';

  String get sunday => en ? 'Sunday' : 'الأحد';

  String get monday => en ? 'Monday' : 'الأثنين';

  String get tuesday => en ? 'Tuesday' : 'الثلاثاء';

  String get wednesday => en ? 'Wednesday' : 'الأربعاء';

  String get thursday => en ? 'Thursday' : 'الخميس';

  String get friday => en ? 'Friday' : 'الجمعة';

  String get closed => en ? 'Closed' : 'مغلق';

  String get undefined => en ? 'Undefined' : 'غير محدد';

  String get edit => en ? 'Edit' : 'تعديل';

  String get startDate => en ? 'Start Date' : 'تاريخ البداية';

  String get endDate => en ? 'End Date' : 'تاريخ النهاية';

  String get reason => en ? 'Reason' : 'السبب';

  String get startTime => en ? 'Start Time' : 'وقت البداية';

  String get endTime => en ? 'End Time' : 'وقت النهاية';

  String get notes => en ? 'Notes' : 'ملاحظات';

  String get show => en ? 'Show' : 'عرض';

  String get insert_email =>
      en ? 'Insert your Email Address' : 'أدخل عنوان البريد الإلكتروني';

  String get insert_mobile => en
      ? 'Insert your Mobile number (11-digits)'
      : 'أدخل رقم الهاتف الخلوي (١١ رقم)';

  String get verification_code => en ? 'Verification Code' : 'كود التحقق';

  String get set_new_password =>
      en ? 'Set New Password' : 'عيّن كلمة مرور جديدة';

  String get the_two_inserted_passwords_are_not_matched => en
      ? 'The two inserted passwords are not matched.'
      : 'كلمتي المرور المدخلتين غير متطابقتين.';

  String get reset_password => en ? 'Reset Password' : 'إستعادة كلمة المرور';

  String get new_password_has_been_sit_successfully => en
      ? 'The new password has been sit successfully. You can now login.'
      : 'تم حفظ كلمة المرور الجديدة بنجاح. يمكنك الآن تسجيل الدخول.';

  String get view_profile => en ? 'View Profile' : 'عرض الملف الشخصي';

  String get request => en ? 'Request' : 'طلب';

  String get readMore => en ? 'Read More' : 'اقرأ المزيد';

  String get write_comment => en ? 'Write a comment...' : 'اترك تعليقك...';

  String get comments => en ? 'Comments' : 'التعليقات';

  String get changePhoto => en ? 'Change Photo' : 'تغيير الصورة';

  String get from_gallery => en ? 'From Gallery' : 'من الاستوديو';

  String get from_camera => en ? 'From Camera' : 'من الكاميرا';

  String get you_must_change_your_password =>
      en ? 'You must change your password.' : 'يجب تغيير كلمة المرور.';

  String get passwordChanged =>
      en ? 'The password has changed.' : 'تم تغيير كلمة المرور.';

  String get write_your_text_here =>
      en ? 'Write your text here...' : 'اكتب النص هنا...';

  String get anonymous => en ? 'Anonymous' : 'غير معروف';

  get num_0 => en ? '0' : '٠';

  get num_1 => en ? '1' : '١';

  get num_2 => en ? '2' : '٢';

  get num_3 => en ? '3' : '٣';

  get num_4 => en ? '4' : '٤';

  get num_5 => en ? '5' : '٥';

  get num_6 => en ? '6' : '٦';

  get num_7 => en ? '7' : '٧';

  get num_8 => en ? '8' : '٨';

  get num_9 => en ? '9' : '٩';

  String get please_enter_valid_username =>
      en ? 'Please enter a valid user name' : 'يرجى إدخال اسم مستخدم صحيح';

  String get settings => en ? 'Settings' : 'الإعدادات';

  String get see_data => en ? 'see data' : 'رؤية البيانات';

  get years => en ? 'years' : 'عام';

  get Y => en ? 'Y' : 'ع';

  get and => en ? 'and' : 'و';

  get months => en ? 'months' : 'شهور';

  get days => en ? 'days' : 'يوم';

  get M => en ? 'M' : 'ش';

  get D => en ? 'D' : 'ي';

  String get by_name => en ? 'By Name' : 'بالإسم';

  get weight => en ? 'Weight' : 'الوزن';

  get kg => en ? 'KG' : 'ك.ج';

  get tall => en ? 'Tall' : 'الطول';

  get cm => en ? 'CM' : 'سم';

  get father => en ? 'Father' : 'الأب';

  get mother => en ? 'Mother' : 'الأم';

  get brothers => en ? 'Brothers' : 'الأخوات';

  String get children => en ? 'Children' : 'الأبناء';

  String get addedBy => en ? 'Added By' : 'أضافها';

  get prefix => en ? 'Prefix' : 'بادئة';

  get prefix_example =>
      en ? 'Prefix (Mr, Mrs, Dr, ... etc)' : 'بادئة (أ، م، د ... إلخ)';

  get choose_photo_source => en ? 'Choose photo source' : 'حدد مصدر الصورة';

  get camera => en ? 'Camera' : 'الكاميرا';

  get gallery => en ? 'Gallery' : 'الاستوديو';

  get which_one => en ? 'Which one?' : 'أيهم تختار؟';

  get first_date => en ? 'First Date' : 'أول تاريخ';

  get last_date => en ? 'Last Date' : 'آخر تاريخ';

  get offset_date => en ? 'Offset Date' : 'تاريخ البداية';

  get end_date => en ? 'End Date' : 'تاريخ النهاية';

  String get by_createDate =>
      en ? 'By Create Date (Asc.)' : 'بتاريخ التسجيل تصاعديا';

  String
      get code_sent_to_EMAILADDRESS_plz_check_and_copy_the_code_to_provided_input_box =>
          en
              ? 'A secret code has been sent to\nEMAILADDRESS\n'
                  'Check inbox/junk, insert the code into the following input box'
              : 'تم إسال كول سري ل\nEMAILADDRESS\n'
                  'تفحّص بريدك الإلكترني، وأدخل الكود في المُدخل التالي.';

  get please_enter_the_correct_code_that_sent_to_ => en
      ? 'Please enter the correct code that sent to '
      : 'برجاء إدخال الكود الصحيح الذي تم ارساله إلى ';

  get your_password_has_been_reset_successfully => en
      ? 'Your password has been reset successfully'
      : 'تم تغيير كلمة المرور بنجاح';

  String get communications => en ? 'Communications' : 'التواصل';

  String get digitalLearning => en ? 'Digital Learning' : 'التعليم الإلكتروني';

  String get homeworks_exams =>
      en ? 'Homeworks / Exams' : 'الواجبات / الإختبارات';

  String get exams => en ? 'Exams' : 'الإختبارات';

  String get homeworks => en ? 'Homeworks' : 'الواجبات';

  get mails => en ? 'Mails' : 'البريد الإلكتروني';

  get chats => en ? 'Chats' : 'المحادثات';

  String get inbox => en ? 'Inbox' : 'الوارد';

  String get sent => en ? 'Sent' : 'المرسل';

  String get draft => en ? 'Draft' : 'المسودة';

  String get archived => en ? 'Archived' : 'المؤرشف';

  String get archive => en ? 'Archive' : 'أرشيف';

  String get deleted => en ? 'Deleted' : 'المحذوف';

  String get select_desired_message =>
      en ? 'Select the desired messages' : 'حدد الرسائل المرغوبة';

  String get deleting => en ? 'Deleting...' : 'جاري الحذف...';

  String get newMail => en ? 'New Mail' : 'رسالة جديدة';

  String get you_dont_have_messages =>
      en ? 'You don\'t have messages' : 'ليس دليك رسايل';

  // String get delete_all_mails_in => en ? 'delete all mails in' : 'حذف كل الرسائل الموجودة في';
  String get delete_selected_X_mails_in => en
      ? 'delete selected mails (X) in'
      : 'حذف الرسائل المحددة (X) الموجودة في';

  String get delete_selected_mail_of_subject => en
      ? 'delete selected mail of subject'
      : 'حذف رسائل البريد المحدد تحت عنوان';

  String get delete_selected_message =>
      en ? 'delete selected message' : 'حذف الرسالة المحددة';

  // String get move_all_mails_to_ => en ? 'move all mails to' : 'نقل كل الرسائل إلى';
  String get move_selected_X_mails_to_ =>
      en ? 'move selected mails (X) to' : 'نقل الرسائل المحددة (X) إلى';

  String get restore_selected_X_mails_to_original_categories => en
      ? 'restore selected mails (X) to original categories'
      : 'إستعادة الرسائل المحددة (X) إلى التصنيفات الأصلية';

  String get move_selected_mail_of_subject => en
      ? 'move selected mail of subject'
      : 'نقل رسائل البريد المحدد تخت عنوان';

  String get those_mails_will_delete_automatically_after_30_days_of_delation_time =>
      en
          ? 'Those mails will delete automatically after 30 days of deletion time'
          : 'سيتم حذف هذه الرسائل تلقائيا بعد مرور ٣٠ يوم من وقت حذفها.';

  get global => en ? 'Global' : 'عامة';

  String get add_reply => en ? 'Add Reply' : 'أضف رد';

  get change_directory => en ? 'Change Directory' : 'تغيير المجلد';

  get thisReplayWillSendTo =>
      en ? 'This replay will send to' : 'سيتم إرسال هذا الرد إلى';

  get attachments => en ? 'Attachments' : 'المرفقات';

  String get dismiss_keyboard_to_see_attachments => en
      ? 'Dismiss keyboard to see the attachments'
      : 'أغلق لوحة المفاتيح لرؤية المرفقات';

  String get please_type_the_message_or_add_attachment => en
      ? 'Please type the message or add attachment'
      : 'يرجى كتابة الرسالة أو إضافة مرفق';

  String get sending => en ? 'Sending...' : 'جاري الإسال...';

  get subject => en ? 'Subject' : 'الموضوع';

  get specifyReceivers => en ? 'Specify Receivers' : 'حدد المرسل لهم';

  get liveTill => en ? 'Live Till' : 'صالحة حتى';

  String get individual => en ? 'Individual' : 'فردي';

  String get groups => en ? 'Groups' : 'مجموعات';

  get the_message_will_delete_automatically_on_selected_datetime => en
      ? 'The message will delete automatically on selected date/time'
      : 'سيتم حذف الرسالة تلقائيا في التريخ والوقت المحددين';

  String get youHaveToSetMessageExpireDate => en
      ? 'You have to set message expire date for global messages.'
      : 'لابد من تحديد تاريخ نهاية الرسالة للرسائل العامة.';

  String get messageExpireDateMustInBeFutureByOneHourAtLeast => en
      ? 'Message expire date must be in the future by one hour at least for global messages.'
      : 'لابد أن يكون تاريخ نهاية الرسالة لاحق للآن بمقدار ساعه على الأقل للرسائل العامة.';

  String get message_subject_is_required =>
      en ? 'Message subject is required' : 'عنوان الرسالة مطلوب';

  String get saveDraft => en ? 'Save As Draft' : 'حفظ كمسودة';

  get image => en ? 'Image' : 'صورة';

  get video => en ? 'Video' : 'فيديو';

  get record => en ? 'Record' : 'تسجيل';

  get unread_messages => en ? 'Unread Messages' : 'رسائل غير مقرؤه';

  String get copy => en ? 'Copy' : 'نسخ';

  get select_one_of_the_following_actions => en
      ? 'Select one of the following actions'
      : 'اختر واحدة من الأوامر التالية';

  get you_cannot_add_different_types_of_files => en
      ? 'You can\'t add different types of files'
      : 'لا يمكن إضافة أنواع محتلفة من الملفات';

  get you_cannot_add_multiple_files =>
      en ? 'You can\'t add multiple files' : 'لا يمكن إضافة أكثر من ملف';

  get you_can_add_one_file_only =>
      en ? 'You can add one file only' : 'يمكنك إضافة ملف واحد فقط';

  String get error_while_uploading_X_file =>
      en ? 'Error while uploading \"_X_\" file' : 'خطأ أثناء حفظ الملف \"_X_\"';

  String get send_failed => en ? 'Send Failed' : 'تعذر الإرسال';

  String get send_failed_click_retry_to_resend => en
      ? 'Send failed, click retry to resend'
      : 'تعذر الإرسال، اضغط إعادة الإسال لإعادة المحاولة';

  String get private_chat => en ? 'Private Chat' : 'محادثة خاصة';

  String get group_chat => en ? 'Group Chat' : 'محادثة جماعية';

  get start_new_chat => en ? 'Start New Chat' : 'بدأ محادثة جديدة';

  String get start_chat => en ? 'Start Chat' : 'ابدأ المحادثة';

  String get enter_name_for_this_chat_group => en
      ? 'Enter a name  for this chat group'
      : 'ادخل إسما لهذه المحادثة الجماعية';

  String get chat_group_name =>
      en ? 'Chat Group Name' : 'اسم المحادثة الجماعية';

  get you_have_to_change_your_password => en
      ? 'You have to change your password'
      : 'يتوجب عليك تغيير كلمة المرور الخاصة بك';

  get you_need_to_register_your_email_address_to_help_you_restore_your_password_if_forget => en
      ? 'You need to register your Email address to help you restore your password if you forget'
      : 'تحتاج لتسجيل عنوان بريدك الإلكتروني لمساعدتك في حالة نسيت كلمة المرور';

  get change_email_address =>
      en ? 'Change Email Address' : 'تغيير البريد الإلكتروني';

  get have_sent_to => en ? 'have sent to' : 'قد تم إرسالة إلى';

  get enter => en ? 'Enter' : 'أدخل';

  String get you_dont_have_notifications =>
      en ? 'You don\'t have notifications' : 'ليس لديك تنبيهات';

  String get you_dont_have => en ? 'You don\'t have' : 'ليس لديك';

  String get you_dont_have_any => en ? 'You don\'t have any' : 'ليس لديك أي';

  String get see_more => en ? 'See More' : 'عرض المزيد';

  String get send_emil => en ? 'Send Mail' : 'ارسل بريد';

  String get chat => en ? 'Chat' : 'محادثة';

  String get lastLoginTime => en ? 'Last Login Time' : 'آخر تسجيل دخول';

  String get teacher_info => en ? 'Teacher Info' : 'معلومات المدرس';

  get floor => en ? 'floor' : 'دور';

  get subjects => en ? 'Subjects' : 'المواد';

  get classrooms => en ? 'Classrooms' : 'الفصول';

  String get student_info => en ? 'Student Info' : 'معلومات الطالب';

  get parent => en ? 'Parent' : 'ولي الأمر';

  get teachers => en ? 'Teachers' : 'المدرسين';

  get teacher => en ? 'Teacher' : 'معلم';

  String get parent_info => en ? 'Parent Info' : 'معلومات ولي الأمر';

  get schoolDepartment => en ? 'Department' : 'القسم';
  get schoolBuilding => en ? 'Building' : 'المبنى';

  get learningStage => en ? 'Learning Stage' : 'المرحلة التعليمية';

  get learningStageLevel => en ? 'Learning Stage Level' : 'المستوى التعليمي';

  get classroom => en ? 'Classroom' : 'الفصل';

  get studdingYear => en ? 'Studding Year' : 'العام الدراسي';

  String get changePhoneNumber =>
      en ? 'Change Phone Number' : 'تغيير رقم الهاتف';

  String get swapAppLanguage => en ? 'Swap App Language' : 'تبديل لغة التطبيق';

  get are_you_still_use_the_selected_image_as_profile => en
      ? 'Are you still want to use the selected image as profile photo?'
      : 'هل مازلت ترغب في استخدام الصورة المختارة كصوررة للملف الشخصي؟';

  String get use => en ? 'Use' : 'استخدم';

  get changing_failed => en ? 'Changing failed' : 'تعذر التغيير';

  get all_chage_you_have_made_will_be_lost => en
      ? 'All changes you have made will be lost'
      : 'كل التغييرات التي قمت بها سيتم فقدها';

  String get show_attachments => en ? 'Show Attachments' : 'أظهر المرفقات';

  String get click_here_to_show_attachments =>
      en ? 'Click here to show attachments' : 'اضغط هنا لإظهار المرفقات';

  get managers => en ? 'Managers' : 'مديرين';

  get the_lessons => en ? 'Lessons' : 'الدروس';

  get the_students => en ? 'Students' : 'الطلاب';

  get students => en ? 'students' : 'طلاب';

  get students2 => en ? 'students' : 'طالب';

  get select_classroom => en ? 'Select classroom' : 'اختار اسم الفصل';

  String get profile => en ? 'Profile' : 'الملف الشخصي';

  get select_subject => en ? 'Select Subject' : 'اختار اسم المادة';

  get printing_file_saved_to =>
      en ? 'Printing file saved to' : 'تم حفظ ملف الطباعه في';

  get cant_open_the_file => en ? 'Can\'t open the file' : 'تعذر فتح الملف';

  String get the_lesson_add_successfully =>
      en ? 'The lesson add successfully' : 'تم اضافة الدرس بنجاح';

  String get the_lesson_updated_successfully =>
      en ? 'The lesson updated successfully' : 'تم تعديل الدرس بنجاح';

  get resources => en ? 'Resources' : 'المصادر';

  String get no_resources =>
      en ? 'No resources available' : 'لا توجود مصادر متاحة';

  get coppied => en ? 'Coppied' : 'تم النسخ';

  get homework => en ? 'Homework' : 'الواجب';

  String get show_questions => en ? 'Show Questions' : 'عرض الأسئلة';

  String get question => en ? 'Question' : 'السؤال';

  String get questions => en ? 'Questions' : 'الأسئلة';

  String get answerers => en ? 'Answerers' : 'الإجابات';

  String get wait_for_review => en ? 'Wait for review' : 'في انتظار المراجعة';

  String get wait_for_review2 => en ? 'wait for review' : 'في انتظار المراجعة';

  String get review => en ? 'Review' : 'مراجعه';

  String get create_virtual_classroom =>
      en ? 'Create Virtual Classroom' : 'انشاء غرفة دراسة افتراضية';

  String get start_virtual_classroom =>
      en ? 'Start Virtual Classroom' : 'بدأ غرفة دراسة افتراضية';

  String get join_virtual_classroom =>
      en ? 'Join Virtual Classroom' : 'انضم لغرفة الدراسة الافتراضية';

  get scheduled_at => en ? 'Scheduled at' : 'جُدوِلَ في';

  get add_new_lesson => en ? 'Add New Lesson' : 'إضافة درس جديد';

  get edit_lesson => en ? 'Edit Lesson' : 'تعديل الدرس';

  get shortDescription => en ? 'Short Description' : 'وصف مختصر';

  String get add_new_resource => en ? 'Add New Resource' : 'إضافة مصدر جديد';

  get choose_source => en ? 'Choose source' : 'حدد المصدر';

  String get localStorage => en ? 'Local Storage' : 'الذاكره المحلية';

  String get webLink => en ? 'Web Link' : 'رابط ويب';

  get fileType => en ? 'File Type' : 'نوع الملف';

  String get add_external_resource =>
      en ? 'Add External Resource' : 'إضافة روابط مصادر خارجية';

  get inserted_link_is_corrupted =>
      en ? 'Inserted link is corrupted' : 'الرابط المستخدم غير صالح';

  String get preview_lesson_content =>
      en ? 'Preview Lesson Content' : 'معاينة محتوى الدرس';

  get font_color => en ? 'Font Color' : 'لون الخط';

  String get long_press_to_paste =>
      en ? 'Long press to paste' : 'ضغطة مطولة للصق من الحافظة';

  String get long_press_paste => en ? 'Long press/paste' : 'ضغطة مطولة\\لصق';
  String get paste_the_link_here => en ? 'Paste the link here' : 'إلصق الرابط هنا';

  String get select_text_to_change_its_format =>
      en ? 'Select text to change its format' : 'حدد النص لتقوم بتغيير تنسيقة';

  String get any_change_to_content_will_lose_the_text_format => en
      ? 'Any change to content will discard the text format'
      : 'أي تغيير في المحتوى سوف يلغي تنسيق النص';

  String get proceed => en ? 'Proceed' : 'واصل';

  get details => en ? 'Details' : 'التفاصيل';

  get questionBank => en ? 'Question Bank' : 'بنك الأسئلة';

  String get questions_added_successfully =>
      en ? 'Questions added successfully' : 'تم اضافة الأسئلة بنجاح';

  get share_question => en ? 'Share Question' : 'شارك بسؤال';

  String get add_new_questions =>
      en ? 'Add New Questions' : 'إضافة أسألة جديدة';

  String get add_new_homework => en ? 'Add New Homework' : 'إضافة واجب جديد';

  String get add_new_exam => en ? 'Add New Exam' : 'إضافة إختبار جديد';

  String get edit_the_questions => en ? 'Edit The Question' : 'تعديل سؤال';

  String get edit_the_homework => en ? 'Edit Homework' : 'تعديل واجب';

  String get edit_the_exam => en ? 'Edit Exam' : 'تعديل الاختبار';

  get suggestedAnswer => en ? 'Suggested Answer' : 'الإجابة المقترحة';

  get add_more => en ? 'Add More' : 'أضف المزيد';

  get questionType => en ? 'Question Type' : 'نوع السؤال';

  get choices => en ? 'Choices' : 'الإختيارات';

  get add_new_choice => en ? 'Add New Choice' : 'إضافة خيار آخر';

  get score => en ? 'Score' : 'الدرجة';

  get time => en ? 'Time' : 'الزمن';

  get min_ => en ? 'Min' : 'د';

  get show_score => en ? 'Show Score' : 'اظهار الدرجة';

  get estimatedTime => en ? 'Estimated Time' : 'الوقت المقدر';

  get elapsedTime => en ? 'Elapsed Time' : 'الوقت المستغرق';

  get estimatedScore => en ? 'Estimated Score' : 'درجة السؤال';

  get example => en ? 'example' : 'مثال';

  get correct => en ? 'Correct' : 'صحيحة';

  get default_is_1 => en ? 'default is 1' : 'الإفتراضي هو ١';

  get you_have_to_add_2_choices_at_least_one_of_them_is_correct_at_least => en
      ? 'You have to add 2 different choices at least, and one of them at least is correct'
      : 'يجب إضافة خيارين مختلفين على الأقل، أحدهم على الأقل يكون صحيحا';

  get one_of_the_choices_must_be_correct_at_least => en
      ? 'One of the choices at least must be marked as correct'
      : 'يجب أن يكون أحد الخيارات على الأقل معلّم كصحيح';

  get only_one_of_the_choices_must_be_correct => en
      ? 'Only one of the choices must be marked as correct'
      : 'يجب أن يكون أحد الخيارات فقط معلّم كصحيح';

  String get please_review_mentioned_errors => en
      ? 'Please review the mentioned errors'
      : 'يرجى مراجعة الأخطاء المذكورة';

  get must_be => en ? 'must be' : 'يجب أن يكون';

  get integer_number => en ? 'integer number' : 'عدد صحيح';

  get find_question => en ? 'Find a question...' : 'أتبحث عن سؤال...';

  get onlyMe => en ? 'Only Me' : 'أنا فقط';

  get unlimited => en ? 'unlimited' : 'غير محدد';

  get add => en ? 'Add' : 'إضافة';

  get number_of_answers => en ? 'No. Of Answers' : 'عدد الإجابات';

  String get lesson_content_formatting =>
      en ? 'Lesson Content Formatting' : 'تنسيق محتوى الدرس';

  String get create_homework_for_lesson =>
      en ? 'Create Homework For The Lesson' : 'إنشاء واجب للدرس';

  String get add_new_homework_for_lesson =>
      en ? 'Add New Homework For The Lesson' : 'إضافة واجب للدرس';

  String get you_didnt_create_homework_for_this_lesson_yet => en
      ? 'You didn\'t create a homework for this lesson yet'
      : 'لم تنشيء واجب لهذا الدرس حتى الآن';

  get supposedTime => en ? 'Supposed Time' : 'زمن الإجابة';

  get where_the_test_will_end_automatically => en
      ? 'where the test will end automatically'
      : 'حيث سيغلق الإختبار تلقائيا بعد انقضاء هذه المده';

  get startAt => en ? 'Start At' : 'تاريخ البدأ';

  get closeAt => en ? 'Close At' : 'تاريخ الإنتهاء';

  get where_no_one_can_answer_any_more => en
      ? 'where no one can answer any more'
      : 'حيث لن يستطيع أحد أن يقدم إجابات بعد ذلك';

  get minimum_time => en ? 'Minimum Time' : 'أقل قيمة للوقت';

  get choose_one => en ? 'Choose one' : 'اختار أحدهم';

  get number_of_questions => en ? 'Number Of Questions' : 'عدد الأسألة';

  String get step => en ? 'Step' : 'خطوة';

  get lesson => en ? 'Lesson' : 'الدرس';

  String get import_from_questionBank =>
      en ? 'Import from Question Bank' : 'استيراد من بنك الأسئلة';

  get selected_questions => en ? 'Selected Questions' : 'الأسئلة المختارة';

  String get there_is_X_question_which_is_already_exist => en
      ? 'There is X question(s) which is already exist and not added.'
      : '.يوجد X سؤال/أسئلة مكررين لم يتم اضافتهم';

  get select_lesson => en ? 'Select Lesson' : 'اختار الدرس';

  get select_learningStageLevel =>
      en ? 'Select Learning Stage Level' : 'اختار اسم المرحلة التعليمية';

  get leave_empty_to_target_all =>
      en ? 'Leave empty to target all' : 'اتركها فارغة لإختيار الجميع';

  String get you_have_to_select_subject_first =>
      en ? 'You have to select subject first' : 'يجب اختيار اسم المادة أولا';

  String get retrieving_data_failed =>
      en ? 'Retrieving data failed' : 'تعذر تحميل الداتا';

  String get subject_and_lesson_is_required =>
      en ? 'Subject and lesson are required' : 'اسم المادة والدرس مطلوبين';

  String get subject_is_required =>
      en ? 'Subject name is required' : 'اسم المادة مطلوب';

  String get supposedTime_must_be_greaterThan_X_minutes => en
      ? 'Supposed time must be greater than X minutes'
      : 'زمن الإجابة يجب ألا يقل عن X دقيقة';

  String get please_put_description_for_the_X =>
      en ? 'Please put a description for the ' : 'برجاء وضع وصف لل';

  String get start_time_is_required =>
      en ? 'Start time is required' : 'يجب تحديد وقت البداية';

  get you_didnt_set_test_time_thats_mean_it_has_open_time => en
      ? 'You didn\'t set the Test Time, that\'s mean it has an open time.'
      : 'لم تحدد وقت البداية للإختبار مما يعني أن مدة الإمتحان مفتوحة.';

  String get close_time_must_be_in_the_future_and_cover_answering_time => en
      ? 'Close time must be in the future and cover answering time to make sure of get enough time for answering the questions'
      : 'تاريخ ووقت الإنتهاء ينبغى أن يكون في المستقبل وأن يغطى زمن الإجابة لضمان الوقت الكافي للإجابة على الأسئلة.';

  String get start_time_must_be_in_the_future_by_at_least_X_minutes => en
      ? 'Start time must be in the future by at least X minutes'
      : 'وقت البدأ ينبغى أن يكون في المستقبل بما لا يقل عن X دقائق';

  get semester => en ? 'Semester' : 'الفصل الدراسي';

  get openTime => en ? 'Open Time' : 'وقت مفتوح';

  get createdAt => en ? 'Created At' : 'تاريخ الإنشاء';

  get createdBy => en ? 'Created By' : 'أنشيء بواسطة';

  get hidden => en ? 'Hidden' : 'مختفي';

  get homework_answers => en ? 'Homework Answers' : 'إحابات الواجبات';

  get exam_answers => en ? 'Exam Answers' : 'إحابات الإختبارات';

  get closeTime => en ? 'Close Time' : 'تاريخ الإنتهاء';

  get leaveReview => en ? 'Leave review' : 'مغادرة المراجعه';

  get unknown => en ? 'Unknow' : 'غير معروف';

  String get set => en ? 'Set' : 'حفظ';

  String get set_score => en ? 'Set Score' : 'حفظ التقييم';

  get type_answer_score_here =>
      en ? 'Type answer score here' : 'اكتب تقييم الإجابة هنا';

  get type_valid_number => en ? 'Type valid number' : 'اكتب رقما صحيحا';

  get max => en ? 'Max.' : 'بحد أقصى';

  String get you_entered_score_greater_than_the_didicated_for_this_question_which_is_X => en
      ? 'You entered score greater than the dedicated for this question which is X'
      : 'لقد أدخلت قيمة أكبر من المخصصة لهذا السؤال والتي هي X';

  get set_the_answer_score => en ? 'Set the answer score' : 'حدد تقييم للإجابة';

  get automatedScore => en ? 'Automated Score' : 'التقييم التلقائي';

  get teacher_score => en ? 'Teacher Score' : 'تقييم المدرس';

  get not_set => '<NOT-SET>';
  get not_set_yet => 'Not set yet';

  String get do_you_have_any_comment =>
      en ? 'Do you have any comment?' : 'هل لديك أي تعليق؟';

  get the_following_answer_need_to_set_score => en
      ? 'The following answer need to set the score'
      : 'الإجابة التالية تحتاج لوضع التقييم له';

  get the_following_answers_need_to_set_score => en
      ? 'The following answers need to set the score'
      : 'الإجابات التالية تحتاج لوضع التقييم لها';

  String get answer_show_result =>
      en ? 'Answer/Show Result' : 'إجابة/عرض النتيجة';

  get answered => en ? 'Answered' : 'الإجابات';

  get questionsCount => en ? 'Questions Count' : 'عدد الأسئلة';

  get startAnsweringTime => en ? 'Start Answering Time' : 'عدد الأسئلة';

  get lastUpdateTime => en ? 'Last Update Time' : 'وقت آخر تحديث';

  get reviewedBy => en ? 'Reviewed By' : 'قام بالمراجعة';

  String get testInterval => en ? 'Test Interval' : 'وقت الإختبار';

  String get this_test_created_by =>
      en ? 'This test created by' : 'تم إنشاء هذا الإختبار من قِبَل';

  String get start => en ? 'Start' : 'ابدأ';

  String get test_has_been_finished =>
      en ? 'Test has been finished' : 'تم انتهاء الإختبار';

  String get answeredQuestions => en ? 'Answered Questions' : 'الأسئلة المجابة';

  get answers => en ? 'answers' : 'إجابة';

  String get show_test_result_on_system =>
      en ? 'Show test result on system' : 'شاهد نتيجة الإختبار على النظام';

  String get there_are_X_answers_are_waiting_for_review => en
      ? 'There are X answers are waiting for review'
      : 'لديك X سؤال في انتظار المراجعة';

  String get there_is_one_answer_is_waiting_for_review => en
      ? 'There is one answer is waiting for review'
      : 'لديك سؤال في انتظار المراجعة';

  String? get the_first => en ? 'First' : 'الأول';

  String? get the_second => en ? 'Second' : 'الثاني';

  String? get the_third => en ? 'Third' : 'الثالث';

  String? get the_fourth => en ? 'Fourth' : 'الرابع';

  get delete_test => en ? 'Delete Test' : 'حذف الإختبار';

  String get finish_review => en ? 'Finish Review' : 'انهاء المراجعة';

  String get statistics => en ? 'Statistics' : 'الإحصائيات';

  get virtualRoomsAttendance => en ? 'V.Room Atten.' : 'حضور الغرف';

  get hours => en ? 'Hours' : 'ساعات';

  String get homeworks_statistics =>
      en ? 'Homeworks Statistics' : 'إحصائيات الواجيات';

  String get exams_statistics =>
      en ? 'Exams Statistics' : 'إحصائيات الإختبارات';

  String get virtualRooms_statistics =>
      en ? 'Virtual Rooms Statistics' : 'إحصائيات غرف لبدراسة';

  get reviewerNote => en ? 'Reviewer Note' : 'ملاحظات المراجع';

  get no_end => en ? 'NO-END' : 'غير منتهي';

  get stuff => en ? 'Stuff' : 'الهيئة التعليمية';

  get top_in_subject => en ? 'Top In Subjects' : 'المتفوق بالمواد';

  get top_in_test => en ? 'Top In Test' : 'المتفوق بالإمتحانات';

  get parents => en ? 'Parents' : 'أولياء الأمور';

  get show_mails => en ? 'Show Mails' : 'الإطلاع على البريد';

  get show_chats => en ? 'Show Chats' : 'الإطلاع على المحادثات';

  String get top_students => en ? 'Top Students' : 'الطلبة المتفوقون';

  String get show_more => en ? 'Show More' : 'أظهر المزيد';

  String get testInfo => en ? 'Test Info' : 'معلومات عن الإختبار';

  get select_one_the_following_tests =>
      en ? 'Select one of the following tests' : 'اختار أحد الأختبارات التالية';

  get generalTest => en ? 'General test' : 'اختبار عام';

  get addedHomeworks => en ? 'Added Homeworks' : 'الواجبات المضافة';

  get addedExams => en ? 'Added Exams' : 'الإختبارات المضافة';

  String get if_yes_the_exam_will_reach_all_students_who_study_same_subject => en
      ? 'If yes, the exam will reach all students who study selected subject'
      : 'في حالت نعم، سيصل الاختبار لكل الطلبة الذين يدرسون نفس المادة';

  get reviewedHomeworks => en ? 'Reviewed Homeworks' : 'الواجبات المراجعة';

  get reviewedExams => en ? 'Reviewed Exams' : 'الإختبارات المراجعة';

  get interaction_with_mails =>
      en ? 'Interaction With Mails' : 'التفاعل مع رسائل البريد';

  get virtualRooms => en ? 'Virtual Rooms' : 'الغرف الإفتراضية';

  String get you_are_seeing_as_manager =>
      en ? 'You are seeing as manager' : 'أنت ترى هذا كمدير';

  get show_lessons => en ? 'Show Lessons' : 'عرض الدروس';

  get show_homework_exams =>
      en ? 'Show Homework/Exams' : 'عرض الواجبات/الإختبارات';

  get show_virtual_rooms => en ? 'Show Virtual Room' : 'عرض الغرف الإفتراضية';

  get show_added_questions =>
      en ? 'Show Added Questions' : 'عرض الأسئلة المضافة';

  String get mails_count => en ? 'Mails Count' : 'عدد البريد الإلكتروني';

  String get chat_rooms_count => en ? 'Chat Rooms Count' : 'عدد غرف المحادثة';

  String get chat_messages_count =>
      en ? 'Chat Messages Count' : 'عدد رسائل المحادثات';

  String get lessonsCount => en ? 'Lessons Count' : 'عدد الدروس';

  String get homeworksCount => en ? 'Homeworks Count' : 'عدد الواجبات';

  String get examsCount => en ? 'Exams Count' : 'عدد الإختبارات';

  String get virtualRoomsCount =>
      en ? 'Virtual Rooms Count' : 'عدد الغرف الإفتراضية';

  get type => en ? 'Type' : 'النوع';

  String get general => en ? 'General' : 'عام';

  get chatMembers => en ? 'Chat Members' : 'أطراف المحادثة';

  get createNewVirtualRoom =>
      en ? 'Create New Virtual Room' : 'انشاء غرفة افتراضية جديدة';

  String get cancelled => en ? 'Cancelled' : 'أُلغِيَت';

  get target_audiences => en ? 'Target Audiences' : 'المستهدفون';

  get duration => en ? 'Duration' : 'المدة';

  get delegatedTo => en ? 'Delegated To' : 'أُنتُدِبَت إلي';

  String get ended => en ? 'Ended' : 'انتهى';

  String get starting_virtual_allowed_before_start_time_by_X_minutes_only => en
      ? 'Starting virtual room allowed before start time by X minutes only.'
      : 'يسمح ببدأ الغرف الإفتراضية قبل وقت البدأ بـ X دقائق فقط.';

  get roomLink => en ? 'Room Link' : 'رابط الغرفة';

  get updatedAt => en ? 'Updated At' : 'وقت التحديث';

  String get select_user => en ? 'Select a User' : 'اختار أحد الحسابات';

  String get try_to_generate_google_meet_from_the_next_button => en
      ? 'Try to generate Google Meet link from the next button'
      : 'حاول إنشاء رابط لجوجل ميت من الزر التالي';

  String get generate_google_meet_link =>
      en ? 'Generate Google Meet Link' : 'أنشيء رابط جوجل Meet';

  String get or_you_can_create_one_and_paste_the_link_to_the_next_input => en
      ? 'or you can create one in your favorite platform and paste the link to the next input'
      : 'أو يمكنك انشاء غرفه على المنصة التي ترغب بها ولصق رابط الغرفه في المُدْخَل التالي';

  String get create_link_and_paste_the_link_to_the_next_input => en
      ? 'Create a link on your favorite platform and paste the link to the next input'
      : 'أنشيء غرفه على المنصة التي ترغب بها وألصق الرابط الخاص بها في المُدْخَل التالي';

  String get link_must_start_with_https => en
      ? 'link must start with \'https://\''
      : 'يجب أن يبدأ الرابط ب \'https://\'';

  String get suggestedPlatforms => en ? 'Suggested Platforms' : 'المنصات المقترحة';

  get the_link_of_the_vertual_room_is_missing => en ?
  'The link of the virtual room is missing' :
  'الرابط الخاص بالغرفه الافتراضية غير موجود';

  String get end => en ? 'End' : 'إنهاء';

  String get endMeeting => en ? 'End Meeting' : 'انهاء الإجتماع';

  String get go_back => en ? 'Go Back' : 'للخلف';

  get going_back_with_ending_meeting_will_not_record_end_time_which_important_for_your_evaluation => en ?
  'Going back without end meeting will not record end time which important for your evaluation.' :
  'الرجوع للخلف بدون انهاء الإجتماع لن يقوم بتسجيل وقت الإنتهاء الضروري لتقييم الآداء الخاص بك.';

  get now => en ? 'now' : 'الآن';

  String get byUser => en ? 'By User' : 'بإختيار المستخدم';

  get open_external => en ? 'Open External' : 'عرض خارجيا';

  String get cancelVirtualRoom => en ? 'Cancel Virtual Room' : 'إلغاء الغرفة الإفتراضية';

  get you_cant_cancel_now_while_meeting_should_be_started => en ?
  'You can\'t cancel now while meeting should be started' :
  'لا يمكنك الإلغاء الآن حيث من المفترض أنه قد تم البدأ';

  String get insert_below_the_reason_of_cancelling => en ? 'Insert below the reason of cancelling' : 'أدخل بالأسفل سبب الإلغاء';

  get select_date_time => en ? 'Select Date/Time' : 'حدد التاريخ/الوقت';

  get subjects_based => en ? 'Subjects Based' : 'طُلّاب مادة دراسية';

  String get all_students_who_study_subject_X => en ? 'All students who study subject \"Xxx\"' : 'كل الصلاب الذين يدرسون مادة \"Xxx\"';

  String get your_students => en ? 'Your Students' : 'طلابك';

  String get select_target_classrooms => en ? 'Select target classroom/s' : 'حدد الفصول الدراسية المستهدفة:';

  String get you_have_X_deleted_messages_click_to_show => en ?
  'You have Xxx deleted messages, click here to show' :
  'لديك Xxx رسائل محذوفه، اضغ هنا لعرضها';

  get click_to_specify_room_link => en ? 'Click to specify the room link' : 'اضغط لتحديد رابط الغرفة';

  String get you_can_set_later => en ? 'You can set it later' : 'يمكن تحديد لا حقا';

  get you_have_to_select_one_classroom_at_least => en ?
  'You have to select one classroom at least' :
  'يجب إختيار فصل دراسي واحد على الأقل';

  String get you_cant_start_before_Xxx_minutes_of_start_time => en ?
  'You can\'t start before Xxx minutes of start time' :
  'لا يمكن البدأ قبل Xxx دقيقة من وقت بداية الإختبار';

  get members => en ? 'Members' : 'الأعضاء';

  get studentsRank => en ? 'Students Rank' : 'ترتيب الطلاب';

  get external_links => en ? 'External Links' : 'روابط خارجية';
  get bls_website => 'BLS Website';
  get banan_website => 'BLS Platform';

  String get who_see_the_message => en ? 'Who see the message' : 'من شاهد الرسالة';

  get every_teacher_should_specify_his_classrooms_and_subjects => en ?
  'Every teacher should specify his classrooms and subjects.' :
  'ينبغي على كل مدرس أن يحدد الفصول والمواد الخاصة به.';

  get set_it_anytime_from_your_profile => en ?
      'Set it anytime from your profile.' :
      'حددها في اي وقت من ملفك الشخصي.';

  get later => en ? 'Later' : 'لاحقا';

  String get update_subjects => en ? 'Update Subjects' : 'تعديل المواد';
  String get update_classrooms => en ? 'Update Classrooms' : 'تعديل الفصول';

  get classSchedule => en ? 'Class Schedule' : 'جدول الحصص';

  get teaching_configurations => en ? 'Teaching Configurations' : 'خيارات التدريس';

  get select => en ? 'Select' : 'حدد';

  String get you_are_not_assigned_to_any_classroom => en ?
  'You are not assigned to any classroom' :
  'لم يتم تخصيص فصول دراسية لك';

  String get assign_classroom => en ? 'Assign Classroom' : 'تخصيص فصل دراسي';

  String get contact_it_team_to_fix_this_issue => en ? 'Contact IT supervisors to fix this issue' : 'تواصل مع مسئولي تكنولوجيا المعلومات لحل هذه المشكلة';

  get editedBy => en ? 'Edited By' : 'عٌدّلت بواسطة';

  get your_gender_is_not_specified_contact_admin_solve_this_problem => en ?
  'Your gender is not specified, please contact the admin to solve this issue.' :
  'نوع الجنس الخاص بك غير محدد، يرجى التواصل مع مدير النظام لحل هذه المشكلة.';

  get reportIssue => en ? 'Report An Issue' : 'الإبلاغ عن مشكلة';

  String get only_teachers_can_send_messages => en ? 'Only teachers can send messages' : 'يمكن للمدرسين فقط ارسال الرسائل';

  get poll => en ? 'Poll' : 'استفتاء';

  get createPoll => en ? 'Create Poll' : 'إنشاء استفتاء';

  String get enter_poll_subject => en ? 'Enter poll subject' : 'ادخل موضوع الإستفتاء';

  String get you_have_to_add_2_choices => en ? 'You have to add 2 choices at least' : 'يجب اضافة اختيارين على الأقل';

  String get showResult => en ? 'Show Result' : 'عرض النتائج';

  String get reactors => en ? 'Reactors' : 'المتفاعلين';
  String get pollSelectors => en ? 'Contributors' : 'المشاركين';

  String get find_message => en ? 'Find a message' : 'ابحث عن رسالة';
}
