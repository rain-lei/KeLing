package com.keling.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToAccessibility: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "设置",
                    style = MaterialTheme.typography.titleLarge,
                    color = InkPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = InkPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PaperBackground
            )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 账户设置
            SettingsSection(title = "账户") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "个人资料",
                    subtitle = "编辑头像、昵称等信息",
                    onClick = onNavigateToProfile
                )
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "账户安全",
                    subtitle = "密码、绑定设备管理",
                    onClick = onNavigateToSecurity
                )
            }
            
            // 隐私与无障碍
            SettingsSection(title = "隐私与辅助功能") {
                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "隐私设置",
                    subtitle = "数据可见性、学习记录",
                    onClick = onNavigateToPrivacy
                )
                SettingsItem(
                    icon = Icons.Default.Accessibility,
                    title = "无障碍",
                    subtitle = "字体大小、高对比度、语音朗读",
                    onClick = onNavigateToAccessibility
                )
            }
            
            // 通知设置
            SettingsSection(title = "通知") {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "通知设置",
                    subtitle = "推送、声音、震动等",
                    onClick = onNavigateToNotification
                )
            }
            
            // 其他
            SettingsSection(title = "其他") {
                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = "存储管理",
                    subtitle = "清除缓存、下载管理",
                    onClick = onNavigateToStorage
                )
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "关于课灵",
                    subtitle = "版本 1.0.0",
                    onClick = onNavigateToAbout
                )
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "帮助与反馈",
                    subtitle = "常见问题、联系我们",
                    onClick = onNavigateToHelp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 退出登录
            NeonOutlinedButton(
                text = "退出登录",
                onClick = { showLogoutDialog = true },
                color = NeonRed,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // 退出确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "确认退出",
                    color = InkPrimary
                )
            },
            text = {
                Text(
                    text = "确定要退出登录吗？",
                    color = InkSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("退出", color = NeonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消", color = InkSecondary)
                }
            },
            containerColor = PaperSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = InkSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        NeonCard(glowColor = PaperBorder) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonBlue,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = InkPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = InkMuted
            )
        }
    }
}

@Composable
private fun SettingsItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonBlue,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonBlue,
                checkedTrackColor = NeonBlue.copy(alpha = 0.3f),
                uncheckedThumbColor = InkMuted,
                uncheckedTrackColor = PaperBorder
            )
        )
    }
}
