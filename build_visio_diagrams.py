from __future__ import annotations

import shutil
import subprocess
import tempfile
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parent
OUT_DIR = ROOT / "_lease_report_build" / "images_visio"
VSDX_PATH = ROOT / "房屋租赁系统大作业图稿.vsdx"
REPORT_PATH = ROOT / "房屋租赁系统软件工程大作业报告.docx"
BACKUP_PATH = ROOT / "房屋租赁系统软件工程大作业报告_before_visio.docx"

IMAGE_ORDER = [
    "dfd_context",
    "dfd_l1",
    "dfd_l2",
    "use_case",
    "sequence",
    "state",
    "system_arch",
    "function_structure",
    "er",
    "admin_home",
    "admin_room",
    "admin_apartment",
    "admin_appointment",
    "admin_agreement",
    "h5_search",
    "h5_room",
    "h5_appointment",
    "h5_my_appointment",
    "h5_user",
    "network",
    "activity",
    "gantt",
    "scm",
]


POWERSHELL_SCRIPT = r'''
param(
    [Parameter(Mandatory = $true)][string]$OutDir,
    [Parameter(Mandatory = $true)][string]$VsdxPath
)

$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force -Path $OutDir | Out-Null

$script:W = 13.232
$script:H = 8.27
$script:XScale = $script:W / 11.69
$script:FONT_TITLE = 30
$script:FONT_NODE = 18
$script:FONT_SMALL = 16
$script:FONT_LABEL = 15
$script:PageIndex = 0
$script:Visio = $null
$script:Doc = $null

function C($hex) {
    $value = $hex.TrimStart("#")
    $r = [Convert]::ToInt32($value.Substring(0, 2), 16)
    $g = [Convert]::ToInt32($value.Substring(2, 2), 16)
    $b = [Convert]::ToInt32($value.Substring(4, 2), 16)
    return "RGB($r,$g,$b)"
}

function Y($topY) {
    return $script:H - $topY
}

function X($leftX) {
    return $leftX * $script:XScale
}

function Set-ShapeStyle($shape, $fill, $line, $fontSize, $weight) {
    if ($fontSize -lt $script:FONT_SMALL -and $shape.Text -ne "") {
        $fontSize = $script:FONT_SMALL
    }
    $shape.CellsU("FillForegnd").FormulaU = C $fill
    $shape.CellsU("LineColor").FormulaU = C $line
    $shape.CellsU("LineWeight").FormulaU = "$weight pt"
    $shape.CellsU("Char.Size").FormulaU = "$fontSize pt"
    $shape.CellsU("Char.Color").FormulaU = C "#0B2545"
    $shape.CellsU("Para.HorzAlign").FormulaU = "1"
    $shape.CellsU("VerticalAlign").FormulaU = "1"
}

function Add-Box($page, $x, $y, $w, $h, $text, $fill = "#F6FAFD", $line = "#335C81", $fontSize = $script:FONT_NODE, $weight = 1.2) {
    if ($text -ne "" -and $h -lt 0.65) {
        $h = 0.65
    }
    $shape = $page.DrawRectangle((X $x), (Y ($y + $h)), (X ($x + $w)), (Y $y))
    $shape.Text = $text
    Set-ShapeStyle $shape $fill $line $fontSize $weight
    return $shape
}

function Add-Text($page, $x, $y, $w, $h, $text, $fontSize = $script:FONT_SMALL, $color = "#0B2545", $align = 1) {
    if ($fontSize -lt $script:FONT_SMALL -and $text -ne "") {
        $fontSize = $script:FONT_SMALL
    }
    if ($text -ne "" -and $h -lt 0.34) {
        $h = 0.34
    }
    $shape = $page.DrawRectangle((X $x), (Y ($y + $h)), (X ($x + $w)), (Y $y))
    $shape.Text = $text
    $shape.CellsU("FillPattern").FormulaU = "0"
    $shape.CellsU("LinePattern").FormulaU = "0"
    $shape.CellsU("Char.Size").FormulaU = "$fontSize pt"
    $shape.CellsU("Char.Color").FormulaU = C $color
    $shape.CellsU("Para.HorzAlign").FormulaU = "$align"
    $shape.CellsU("VerticalAlign").FormulaU = "1"
    return $shape
}

function Add-Line($page, $x1, $y1, $x2, $y2, $label = "", $endArrow = $true) {
    $line = $page.DrawLine((X $x1), (Y $y1), (X $x2), (Y $y2))
    $line.CellsU("LineColor").FormulaU = C "#335C81"
    $line.CellsU("LineWeight").FormulaU = "1.3 pt"
    if ($endArrow) {
        $line.CellsU("EndArrow").FormulaU = "13"
    }
    return $line
}

function Add-Polyline($page, $points, $endArrow = $false) {
    for ($i = 0; $i -lt $points.Count - 1; $i++) {
        $p1 = $points[$i]
        $p2 = $points[$i + 1]
        $isLast = ($i -eq $points.Count - 2)
        Add-Line $page $p1[0] $p1[1] $p2[0] $p2[1] "" ($endArrow -and $isLast) | Out-Null
    }
}

function Add-EdgeLabel($page, $x, $y, $w, $h, $text) {
    $shape = $page.DrawRectangle((X $x), (Y ($y + $h)), (X ($x + $w)), (Y $y))
    $shape.Text = $text
    $shape.CellsU("FillPattern").FormulaU = "0"
    $shape.CellsU("LinePattern").FormulaU = "0"
    $shape.CellsU("Char.Size").FormulaU = "$script:FONT_LABEL pt"
    $shape.CellsU("Char.Color").FormulaU = C "#0B2545"
    $shape.CellsU("Para.HorzAlign").FormulaU = "1"
    $shape.CellsU("VerticalAlign").FormulaU = "1"
    return $shape
}

function Add-Title($page, $title) {
    Add-Text $page 0.45 0.2 7.0 0.65 $title $script:FONT_TITLE "#0B2545" 0 | Out-Null
    $line = $page.DrawLine((X 0.45), (Y 1.0), (X 11.24), (Y 1.0))
    $line.CellsU("LineColor").FormulaU = C "#D9E2EC"
    $line.CellsU("LineWeight").FormulaU = "1.6 pt"
}

function New-Page($name, $title) {
    if ($script:PageIndex -eq 0) {
        $page = $script:Doc.Pages.Item(1)
    } else {
        $page = $script:Doc.Pages.Add()
    }
    $script:PageIndex += 1
    $page.Name = $name
    $page.PageSheet.CellsU("PageWidth").FormulaU = "$script:W in"
    $page.PageSheet.CellsU("PageHeight").FormulaU = "$script:H in"
    $background = $page.DrawRectangle(0, 0, $script:W, $script:H)
    $background.CellsU("FillForegnd").FormulaU = C "#FFFFFF"
    $background.CellsU("LinePattern").FormulaU = "0"
    $background.SendToBack()
    Add-Title $page $title
    return $page
}

function Export-Page($page, $fileName) {
    $page.Export((Join-Path $OutDir "$fileName.png")) | Out-Null
}

function Draw-Flow($page, $items) {
    for ($i = 0; $i -lt $items.Count; $i++) {
        $item = $items[$i]
        Add-Box $page $item[0] $item[1] $item[2] $item[3] $item[4] "#F6FAFD" "#335C81" 11 | Out-Null
        if ($i -gt 0) {
            $prev = $items[$i - 1]
            Add-Line $page ($prev[0] + $prev[2]) ($prev[1] + $prev[3] / 2) $item[0] ($item[1] + $item[3] / 2) | Out-Null
        }
    }
}

function Draw-Placeholder($name, $title, $subtitle) {
    $page = New-Page $name $title
    Add-Box $page 1.5 1.55 8.7 5.55 "" "#F9FAFB" "#94A3B8" 12 1.1 | Out-Null
    Add-Box $page 2.25 2.25 7.2 3.95 $subtitle "#FFFFFF" "#CBD5E1" 16 0.9 | Out-Null
    Add-Text $page 2.35 5.25 7.0 0.9 "此处用于替换为项目运行后的真实 UI 截图；当前保留统一尺寸与说明，便于报告排版。" 11 "#475569" 1 | Out-Null
    Export-Page $page $name
}

try {
    $script:Visio = New-Object -ComObject Visio.Application
    $script:Visio.Visible = $false
    $script:Visio.AlertResponse = 7
    $script:Doc = $script:Visio.Documents.Add("")

    $p = New-Page "dfd_context" "顶层 DFD 图"
    Add-Box $p 0.75 1.85 1.55 1.0 "租客" "#FFFFFF" "#335C81" 13 | Out-Null
    Add-Box $p 0.75 5.15 1.55 1.0 "运营管理员" "#FFFFFF" "#335C81" 12 | Out-Null
    Add-Box $p 4.45 2.8 3.05 1.75 "房屋租赁系统`n0" "#E8EEF5" "#335C81" 14 | Out-Null
    Add-Box $p 9.35 1.85 1.55 1.0 "短信服务" "#FFFFFF" "#335C81" 12 | Out-Null
    Add-Box $p 9.35 5.15 1.55 1.0 "对象存储" "#FFFFFF" "#335C81" 12 | Out-Null
    Add-Line $p 2.3 2.35 4.45 3.35 | Out-Null
    Add-EdgeLabel $p 2.55 1.72 1.95 0.38 "找房/预约/租约请求" | Out-Null
    Add-Line $p 2.3 5.65 4.45 4.05 | Out-Null
    Add-EdgeLabel $p 2.55 5.92 1.95 0.38 "房源/预约/合同管理" | Out-Null
    Add-Line $p 7.5 3.25 9.35 2.35 | Out-Null
    Add-EdgeLabel $p 7.58 1.82 1.45 0.38 "验证码请求" | Out-Null
    Add-Line $p 9.35 5.65 7.5 4.15 | Out-Null
    Add-EdgeLabel $p 7.7 5.96 1.5 0.38 "图片上传/访问" | Out-Null
    Export-Page $p "dfd_context"

    $p = New-Page "dfd_l1" "1 层 DFD 图"
    $proc = @(
        @(0.7,1.55,1.7,0.8,"1. 用户认证"),
        @(3.0,1.55,1.7,0.8,"2. 房源查询"),
        @(5.3,1.55,1.7,0.8,"3. 预约管理"),
        @(2.0,4.75,1.7,0.8,"4. 租约管理"),
        @(4.7,4.75,1.7,0.8,"5. 后台运营")
    )
    foreach ($x in $proc) { Add-Box $p $x[0] $x[1] $x[2] $x[3] $x[4] | Out-Null }
    $stores = @(
        @(8.7,1.2,"D1 用户库"),
        @(8.7,2.85,"D2 房源库"),
        @(8.7,4.5,"D3 预约库"),
        @(8.7,6.15,"D4 租约库")
    )
    foreach ($s in $stores) { Add-Box $p $s[0] $s[1] 2.05 0.85 $s[2] "#FFF7E6" "#C17C0A" | Out-Null }
    Add-Line $p 2.4 1.95 3.0 1.95 | Out-Null
    Add-Line $p 4.7 1.95 5.3 1.95 | Out-Null
    Add-Line $p 7.0 1.95 8.7 1.51 | Out-Null
    Add-Line $p 7.0 1.95 8.7 3.16 | Out-Null
    Add-Line $p 7.0 1.95 8.7 4.81 | Out-Null
    Add-Line $p 6.4 5.15 8.7 6.46 | Out-Null
    Add-Line $p 3.7 5.15 4.7 5.15 | Out-Null
    Add-Line $p 3.85 2.35 2.85 4.75 | Out-Null
    Export-Page $p "dfd_l1"

    $p = New-Page "dfd_l2" "2 层 DFD 图：房源查询子过程"
    $steps = @(
        @(0.55,2.8,1.55,0.85,"输入筛选条件"),
        @(2.35,2.8,1.85,0.85,"区域/价格/标签过滤"),
        @(4.45,2.8,1.75,0.85,"读取公寓与房间"),
        @(6.45,2.8,1.9,0.85,"组合费用/图片/租期"),
        @(8.7,2.8,1.7,0.85,"返回列表与详情")
    )
    Draw-Flow $p $steps
    Add-Box $p 4.65 5.35 2.1 0.85 "D2 房源库" "#FFF7E6" "#C17C0A" | Out-Null
    Add-Box $p 7.0 5.35 2.1 0.85 "D5 图片库" "#FFF7E6" "#C17C0A" | Out-Null
    Add-Line $p 5.32 3.65 5.7 5.35 | Out-Null
    Add-EdgeLabel $p 4.35 4.55 0.95 0.38 "房源数据" | Out-Null
    Add-Line $p 7.4 3.65 8.05 5.35 | Out-Null
    Add-EdgeLabel $p 7.05 4.55 0.95 0.38 "图片地址" | Out-Null
    Add-Box $p 9.65 4.25 1.8 0.85 "D6 浏览历史" "#FFF7E6" "#C17C0A" | Out-Null
    Add-Line $p 9.55 3.65 9.65 4.67 | Out-Null
    Add-EdgeLabel $p 10.0 3.65 0.95 0.38 "记录浏览" | Out-Null
    Export-Page $p "dfd_l2"

    $p = New-Page "use_case" "用户角色用例图"
    Add-Box $p 3.75 1.25 4.75 5.95 "" "#F9FAFB" "#94A3B8" | Out-Null
    Add-Text $p 5.35 1.55 1.55 0.42 "房屋租赁系统" $script:FONT_NODE "#0B2545" 1 | Out-Null
    Add-Box $p 0.7 2.1 1.2 0.8 "租客" "#FFFFFF" "#335C81" 12 | Out-Null
    Add-Box $p 0.7 4.6 1.2 0.8 "运营管理员" "#FFFFFF" "#335C81" 11 | Out-Null
    Add-Box $p 9.65 3.35 1.3 0.8 "系统管理员" "#FFFFFF" "#335C81" 11 | Out-Null
    $cases = @(
        @(4.2,1.95,"登录认证"),
        @(5.95,2.7,"房源搜索"),
        @(4.2,3.45,"预约看房"),
        @(5.95,4.2,"租约查看"),
        @(4.2,5.05,"房源维护"),
        @(5.95,5.8,"预约处理"),
        @(4.7,6.45,"角色权限管理")
    )
    foreach ($c in $cases) {
        $s = $p.DrawOval((X $c[0]), (Y ($c[1] + 0.68)), (X ($c[0] + 1.75)), (Y $c[1]))
        $s.Text = $c[2]
        Set-ShapeStyle $s "#FFFFFF" "#335C81" $script:FONT_SMALL 1
    }
    Add-Polyline $p @(@(1.9,2.5),@(3.55,2.5),@(3.55,2.29),@(4.2,2.29)) | Out-Null
    Add-Polyline $p @(@(1.9,2.5),@(3.45,2.5),@(3.45,3.04),@(5.95,3.04)) | Out-Null
    Add-Polyline $p @(@(1.9,2.5),@(3.35,2.5),@(3.35,3.79),@(4.2,3.79)) | Out-Null
    Add-Polyline $p @(@(1.9,2.5),@(3.25,2.5),@(3.25,4.54),@(5.95,4.54)) | Out-Null
    Add-Polyline $p @(@(1.9,5.0),@(3.55,5.0),@(3.55,5.39),@(4.2,5.39)) | Out-Null
    Add-Polyline $p @(@(1.9,5.0),@(3.45,5.0),@(3.45,6.14),@(5.95,6.14)) | Out-Null
    Add-Polyline $p @(@(1.9,5.0),@(3.35,5.0),@(3.35,6.79),@(4.7,6.79)) | Out-Null
    Add-Polyline $p @(@(9.65,3.75),@(8.85,3.75),@(8.85,6.79),@(6.45,6.79)) | Out-Null
    Export-Page $p "use_case"

    $p = New-Page "sequence" "预约看房顺序图"
    $actors = @(
        @(0.75,"租客"),
        @(2.45,"H5 前端"),
        @(4.15,"预约控制器"),
        @(5.95,"预约服务"),
        @(7.65,"数据库"),
        @(9.25,"通知服务")
    )
    foreach ($a in $actors) {
        Add-Box $p $a[0] 1.25 1.45 0.75 $a[1] "#FFFFFF" "#335C81" | Out-Null
        Add-Line $p ($a[0] + 0.725) 2.0 ($a[0] + 0.725) 7.15 "" $false | Out-Null
    }
    $msgs = @(
        @(1.38,2.65,2.45,2.65,"提交预约信息"),
        @(3.08,3.35,4.15,3.35,"POST /appointment"),
        @(4.78,4.05,5.95,4.05,"校验房间与时间"),
        @(6.58,4.75,7.65,4.75,"写入预约记录"),
        @(6.58,5.45,9.25,5.45,"发送提醒"),
        @(7.65,6.35,2.45,6.35,"返回预约结果")
    )
    foreach ($m in $msgs) { Add-Line $p $m[0] $m[1] $m[2] $m[3] | Out-Null }
    Add-EdgeLabel $p 1.58 2.08 1.35 0.38 "提交预约信息" | Out-Null
    Add-EdgeLabel $p 3.2 2.68 1.6 0.38 "POST /appointment" | Out-Null
    Add-EdgeLabel $p 5.0 3.38 1.55 0.38 "校验房间与时间" | Out-Null
    Add-EdgeLabel $p 6.82 4.08 1.45 0.38 "写入预约记录" | Out-Null
    Add-EdgeLabel $p 8.55 4.78 1.0 0.38 "发送提醒" | Out-Null
    Add-EdgeLabel $p 5.08 5.58 1.45 0.38 "返回预约结果" | Out-Null
    Export-Page $p "sequence"

    $p = New-Page "state" "租约状态图"
    $states = @(
        @(0.9,3.15,1.35,0.7,"待签约"),
        @(3.0,3.15,1.35,0.7,"已签约"),
        @(5.1,3.15,1.35,0.7,"执行中"),
        @(7.2,2.3,1.35,0.7,"已到期"),
        @(7.2,4.1,1.35,0.7,"已取消")
    )
    foreach ($s in $states) { Add-Box $p $s[0] $s[1] $s[2] $s[3] $s[4] | Out-Null }
    Add-Line $p 2.25 3.5 3.0 3.5 | Out-Null
    Add-EdgeLabel $p 2.28 2.55 1.0 0.38 "提交合同" | Out-Null
    Add-Line $p 4.35 3.5 5.1 3.5 | Out-Null
    Add-EdgeLabel $p 4.45 2.55 0.75 0.38 "生效" | Out-Null
    Add-Line $p 6.45 3.5 7.2 2.65 | Out-Null
    Add-EdgeLabel $p 6.02 2.32 0.7 0.38 "到期" | Out-Null
    Add-Line $p 6.45 3.5 7.2 4.45 | Out-Null
    Add-EdgeLabel $p 6.54 4.82 1.1 0.38 "退租/作废" | Out-Null
    Add-Text $p 0.9 2.15 0.65 0.35 "开始" 9 | Out-Null
    Add-Line $p 1.55 2.32 1.58 3.15 "" | Out-Null
    Export-Page $p "state"

    $p = New-Page "system_arch" "系统架构图"
    Add-Text $p 0.75 1.15 1.2 0.3 "前端层" 11 "#475569" 0 | Out-Null
    Add-Box $p 1.45 1.45 2.1 0.95 "H5 租客端`nVue3 + Vant" | Out-Null
    Add-Box $p 1.45 2.85 2.1 0.95 "后台管理端`nVue3 + Element Plus" | Out-Null
    Add-Text $p 4.1 1.15 1.2 0.3 "接口层" 11 "#475569" 0 | Out-Null
    Add-Box $p 4.35 1.45 2.15 0.95 "Web-App API`nSpring Boot 3" | Out-Null
    Add-Box $p 4.35 2.85 2.15 0.95 "Web-Admin API`nSpring Boot 3" | Out-Null
    Add-Text $p 7.0 1.15 1.4 0.3 "公共与持久层" 11 "#475569" 0 | Out-Null
    Add-Box $p 7.25 1.45 2.35 0.95 "公共模块`nJWT / 异常 / MinIO" | Out-Null
    Add-Box $p 7.25 2.85 2.35 0.95 "持久层`nMyBatis-Plus Mapper" | Out-Null
    Add-Box $p 2.0 5.75 1.45 0.7 "MySQL 8.0" "#F6FAFD" "#335C81" 11 | Out-Null
    Add-Box $p 5.0 5.75 1.45 0.7 "Redis 7" "#F6FAFD" "#335C81" 11 | Out-Null
    Add-Box $p 8.0 5.75 1.65 0.7 "MinIO 对象存储" "#F6FAFD" "#335C81" 10 | Out-Null
    Add-Line $p 3.55 1.92 4.35 1.92 | Out-Null
    Add-Line $p 3.55 3.32 4.35 3.32 | Out-Null
    Add-Line $p 6.5 1.92 7.25 1.92 | Out-Null
    Add-Line $p 6.5 3.32 7.25 3.32 | Out-Null
    Add-Line $p 8.42 3.8 2.72 5.75 | Out-Null
    Add-Line $p 8.42 3.8 5.72 5.75 | Out-Null
    Add-Line $p 8.42 3.8 8.82 5.75 | Out-Null
    Export-Page $p "system_arch"

    $p = New-Page "function_structure" "系统功能结构图"
    Add-Box $p 4.55 1.25 2.25 0.65 "房屋租赁系统" "#E8EEF5" "#335C81" 13 | Out-Null
    $mods = @(
        @(0.55,3.0,"租客端","找房搜索`n房间详情`n预约看房`n我的租约`n浏览历史"),
        @(2.65,3.0,"房源管理","公寓维护`n房间维护`n属性维护`n标签维护`n费用维护"),
        @(4.75,3.0,"租赁业务","预约处理`n租约签订`n租约状态`n支付方式`n租期管理"),
        @(6.85,3.0,"系统管理","用户管理`n岗位管理`n角色菜单`n权限控制`n登录认证"),
        @(8.95,3.0,"基础支撑","区域数据`n文件上传`n缓存`n对象存储`n接口文档")
    )
    foreach ($m in $mods) {
        Add-Box $p $m[0] $m[1] 1.55 3.0 "$($m[2])`n`n$($m[3])" "#FFFFFF" "#335C81" 9 | Out-Null
        Add-Line $p 5.67 1.9 ($m[0] + 0.78) 3.0 "" | Out-Null
    }
    Export-Page $p "function_structure"

    $p = New-Page "er" "数据库 ER/关系概览图"
    $entities = @(
        @(0.75,1.45,"用户`nuser_info"),
        @(3.0,1.45,"公寓`napartment_info"),
        @(5.25,1.45,"房间`nroom_info"),
        @(7.5,1.45,"图片`ngraph_info"),
        @(1.85,3.75,"预约`nview_appointment"),
        @(4.1,3.75,"租约`nlease_agreement"),
        @(6.35,3.75,"属性/设施/标签`nattr/facility/label"),
        @(8.6,3.75,"系统权限`nrole/menu/post")
    )
    foreach ($e in $entities) { Add-Box $p $e[0] $e[1] 1.75 0.85 $e[2] "#FFFDF7" "#C17C0A" 9 | Out-Null }
    Add-Line $p 1.62 2.3 2.72 3.75 | Out-Null
    Add-EdgeLabel $p 2.2 2.38 0.7 0.38 "提交" | Out-Null
    Add-Line $p 4.75 1.88 5.25 1.88 | Out-Null
    Add-EdgeLabel $p 4.65 2.35 0.7 0.38 "包含" | Out-Null
    Add-Line $p 7.0 1.88 7.5 1.88 | Out-Null
    Add-EdgeLabel $p 6.95 1.02 1.0 0.38 "关联图片" | Out-Null
    Add-Line $p 3.6 4.17 4.1 4.17 | Out-Null
    Add-EdgeLabel $p 3.48 4.7 1.0 0.38 "生成租约" | Out-Null
    Add-Line $p 6.12 2.3 7.22 3.75 | Out-Null
    Add-EdgeLabel $p 5.58 3.1 0.7 0.38 "描述" | Out-Null
    Add-Line $p 0.75 1.88 1.85 4.17 | Out-Null
    Add-EdgeLabel $p 0.12 3.15 1.0 0.38 "发起预约" | Out-Null
    Export-Page $p "er"

    Draw-Placeholder "admin_home" "后台管理端：首页截图占位" "后台管理端：首页"
    Draw-Placeholder "admin_room" "后台管理端：房间管理截图占位" "后台管理端：房间管理"
    Draw-Placeholder "admin_apartment" "后台管理端：公寓管理截图占位" "后台管理端：公寓管理"
    Draw-Placeholder "admin_appointment" "后台管理端：看房预约管理截图占位" "后台管理端：看房预约管理"
    Draw-Placeholder "admin_agreement" "后台管理端：租约管理截图占位" "后台管理端：租约管理"
    Draw-Placeholder "h5_search" "H5 租客端：找房页面截图占位" "H5 租客端：找房页面"
    Draw-Placeholder "h5_room" "H5 租客端：房间详情截图占位" "H5 租客端：房间详情"
    Draw-Placeholder "h5_appointment" "H5 租客端：预约看房截图占位" "H5 租客端：预约看房"
    Draw-Placeholder "h5_my_appointment" "H5 租客端：我的预约截图占位" "H5 租客端：我的预约"
    Draw-Placeholder "h5_user" "H5 租客端：个人中心截图占位" "H5 租客端：个人中心"

    $p = New-Page "network" "网络与部署设计图"
    $nodes = @(
        @(0.8,2.15,"用户浏览器/移动端"),
        @(2.9,2.15,"Nginx/静态资源"),
        @(5.05,2.15,"后端 API 服务`n8080/8081"),
        @(7.45,1.35,"MySQL`n3307"),
        @(7.45,2.85,"Redis`n6379"),
        @(7.45,4.35,"MinIO`n9000/9001")
    )
    foreach ($n in $nodes) { Add-Box $p $n[0] $n[1] 1.6 0.78 $n[2] "#F6FAFD" "#335C81" 10 | Out-Null }
    Add-Line $p 2.4 2.54 2.9 2.54 | Out-Null
    Add-Line $p 4.5 2.54 5.05 2.54 | Out-Null
    Add-Line $p 6.65 2.54 7.45 1.74 | Out-Null
    Add-Line $p 6.65 2.54 7.45 3.24 | Out-Null
    Add-Line $p 6.65 2.54 7.45 4.74 | Out-Null
    Add-Text $p 2.0 6.3 7.8 0.45 "本地开发通过 Docker Compose 提供 MySQL、Redis、MinIO；部署时由 Nginx 承载前端静态资源并反向代理后端 API。" 11 "#475569" 1 | Out-Null
    Export-Page $p "network"

    $p = New-Page "activity" "预约看房活动图"
    Add-Text $p 0.85 1.1 1.2 0.3 "租客" 10 "#475569" 1 | Out-Null
    Add-Text $p 4.15 1.1 1.2 0.3 "系统" 10 "#475569" 1 | Out-Null
    Add-Text $p 7.4 1.1 1.4 0.3 "运营管理员" 10 "#475569" 1 | Out-Null
    $acts = @(
        @(0.75,1.65,1.55,0.55,"进入找房页"),
        @(0.75,2.45,1.55,0.55,"筛选区域/价格"),
        @(0.75,3.25,1.55,0.55,"查看房间详情"),
        @(3.85,3.25,1.65,0.55,"提交预约信息"),
        @(3.85,4.15,1.65,0.55,"校验并保存预约"),
        @(7.15,4.15,1.65,0.55,"后台确认预约"),
        @(7.15,5.05,1.65,0.55,"到店看房"),
        @(3.85,5.75,1.65,0.55,"签署租约"),
        @(7.15,5.95,1.65,0.55,"结束/取消")
    )
    foreach ($a in $acts) { Add-Box $p $a[0] $a[1] $a[2] $a[3] $a[4] | Out-Null }
    Add-Line $p 1.52 2.2 1.52 2.45 | Out-Null
    Add-Line $p 1.52 3.0 1.52 3.25 | Out-Null
    Add-Line $p 2.3 3.53 3.85 3.53 | Out-Null
    Add-Line $p 4.68 3.8 4.68 4.15 | Out-Null
    Add-Line $p 5.5 4.43 7.15 4.43 | Out-Null
    Add-Line $p 7.98 4.7 7.98 5.05 | Out-Null
    Add-Line $p 7.15 5.33 5.5 6.02 | Out-Null
    Add-EdgeLabel $p 5.05 4.9 1.0 0.38 "确认租赁" | Out-Null
    Add-Line $p 7.98 5.6 7.98 5.95 | Out-Null
    Add-EdgeLabel $p 8.75 5.38 0.7 0.38 "取消" | Out-Null
    Export-Page $p "activity"

    $p = New-Page "gantt" "项目实施甘特图"
    Add-Text $p 0.65 1.25 1.2 0.35 "任务" 12 "#0B2545" 0 | Out-Null
    $x0 = 2.45
    $cell = 0.58
    for ($i = 1; $i -le 14; $i++) {
        Add-Text $p ($x0 + ($i - 1) * $cell) 1.25 0.4 0.3 "$i" 10 "#0B2545" 1 | Out-Null
        Add-Line $p ($x0 + ($i - 1) * $cell) 1.6 ($x0 + ($i - 1) * $cell) 6.95 "" $false | Out-Null
    }
    $tasks = @(
        @("立项与调研",1,2),
        @("需求分析",2,4),
        @("概要/数据库设计",4,6),
        @("后台管理开发",6,9),
        @("H5 租客端开发",7,10),
        @("接口联调",10,11),
        @("测试与修复",11,13),
        @("文档与汇报",13,14)
    )
    for ($r = 0; $r -lt $tasks.Count; $r++) {
        $y = 1.85 + $r * 0.62
        Add-Text $p 0.65 $y 1.75 0.32 $tasks[$r][0] 10 "#0B2545" 0 | Out-Null
        $start = [int]$tasks[$r][1]
        $end = [int]$tasks[$r][2]
        Add-Box $p ($x0 + ($start - 1) * $cell) ($y + 0.05) (($end - $start + 1) * $cell) 0.28 "" "#4F7CAC" "#4F7CAC" 8 0.5 | Out-Null
    }
    Add-Text $p 2.45 7.25 1.2 0.3 "单位：周" 9 "#555555" 0 | Out-Null
    Export-Page $p "gantt"

    Draw-Placeholder "scm" "软件配置管理工具截图占位" "Git / 远程仓库 / 分支管理"

    if (Test-Path -LiteralPath $VsdxPath) {
        Remove-Item -LiteralPath $VsdxPath -Force
    }
    $script:Doc.SaveAs($VsdxPath) | Out-Null
    Write-Output "VISIO_OK VSDX=$VsdxPath OUT=$OutDir PAGES=$script:PageIndex VERSION=$($script:Visio.Version)"
}
finally {
    if ($script:Doc -ne $null) {
        $script:Doc.Close() | Out-Null
        [System.Runtime.InteropServices.Marshal]::ReleaseComObject($script:Doc) | Out-Null
    }
    if ($script:Visio -ne $null) {
        $script:Visio.Quit() | Out-Null
        [System.Runtime.InteropServices.Marshal]::ReleaseComObject($script:Visio) | Out-Null
    }
    [GC]::Collect()
    [GC]::WaitForPendingFinalizers()
}
'''


def run_visio_export() -> None:
    OUT_DIR.parent.mkdir(parents=True, exist_ok=True)
    temp_out_dir = OUT_DIR.parent / f"{OUT_DIR.name}_tmp"
    if temp_out_dir.exists():
        shutil.rmtree(temp_out_dir)
    temp_out_dir.mkdir(parents=True, exist_ok=True)

    with tempfile.NamedTemporaryFile(
        "w",
        suffix=".ps1",
        delete=False,
        encoding="utf-8-sig",
        newline="\r\n",
    ) as script_file:
        script_file.write(POWERSHELL_SCRIPT)
        ps1_path = Path(script_file.name)

    try:
        result = subprocess.run(
            [
                "powershell",
                "-NoProfile",
                "-ExecutionPolicy",
                "Bypass",
                "-File",
                str(ps1_path),
                "-OutDir",
                str(temp_out_dir),
                "-VsdxPath",
                str(VSDX_PATH),
            ],
            cwd=ROOT,
            text=True,
            capture_output=True,
            check=False,
        )
        print(result.stdout, end="")
        if result.stderr:
            print(result.stderr, end="")
        if result.returncode != 0:
            raise RuntimeError(f"Visio export failed with exit code {result.returncode}")
        exported = sorted(temp_out_dir.glob("*.png"))
        if len(exported) != len(IMAGE_ORDER):
            raise RuntimeError(
                f"Expected {len(IMAGE_ORDER)} PNGs, found {len(exported)}"
            )
        if OUT_DIR.exists():
            shutil.rmtree(OUT_DIR)
        temp_out_dir.replace(OUT_DIR)
    finally:
        ps1_path.unlink(missing_ok=True)
        if temp_out_dir.exists():
            shutil.rmtree(temp_out_dir)


def replace_report_images() -> None:
    if not REPORT_PATH.exists():
        raise FileNotFoundError(REPORT_PATH)
    missing = [name for name in IMAGE_ORDER if not (OUT_DIR / f"{name}.png").exists()]
    if missing:
        raise FileNotFoundError(f"Missing exported PNGs: {', '.join(missing)}")

    if not BACKUP_PATH.exists():
        shutil.copy2(REPORT_PATH, BACKUP_PATH)

    temp_docx = REPORT_PATH.with_suffix(".visio.tmp.docx")
    replacements = {
        f"word/media/image{i}.png": (OUT_DIR / f"{name}.png").read_bytes()
        for i, name in enumerate(IMAGE_ORDER, start=1)
    }

    with zipfile.ZipFile(REPORT_PATH, "r") as zin:
        names = zin.namelist()
        missing_targets = [target for target in replacements if target not in names]
        if missing_targets:
            raise FileNotFoundError(
                f"Report media targets missing: {', '.join(missing_targets)}"
            )
        with zipfile.ZipFile(temp_docx, "w", compression=zipfile.ZIP_DEFLATED) as zout:
            for item in zin.infolist():
                data = replacements.get(item.filename)
                if data is None:
                    data = zin.read(item.filename)
                zout.writestr(item, data)

    temp_docx.replace(REPORT_PATH)
    print(f"REPORT_OK path={REPORT_PATH} backup={BACKUP_PATH}")


def verify_outputs() -> None:
    exported = sorted(OUT_DIR.glob("*.png"))
    if len(exported) != len(IMAGE_ORDER):
        raise RuntimeError(f"Expected {len(IMAGE_ORDER)} PNGs, found {len(exported)}")
    if not VSDX_PATH.exists() or VSDX_PATH.stat().st_size == 0:
        raise RuntimeError("VSDX file was not created")
    if not REPORT_PATH.exists() or REPORT_PATH.stat().st_size == 0:
        raise RuntimeError("Report file is missing after replacement")
    print(f"VERIFY_OK pngs={len(exported)} vsdx_bytes={VSDX_PATH.stat().st_size} report_bytes={REPORT_PATH.stat().st_size}")


def main() -> None:
    run_visio_export()
    replace_report_images()
    verify_outputs()


if __name__ == "__main__":
    main()
