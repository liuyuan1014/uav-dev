#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
无人机任务并发抢单测试脚本
测试 Redisson 分布式锁 + MyBatis-Plus 乐观锁的并发控制效果
"""

import requests
import threading
import time
from datetime import datetime

# ==================== 配置区域 ====================
# API 地址
BASE_URL = "http://localhost:8081"
ACCEPT_URL = f"{BASE_URL}/api/uav/mission/accept"

# 测试参数
MISSION_ID = 7  # 要抢的任务ID（修改为你创建的任务ID）
THREAD_COUNT = 100  # 并发线程数（模拟100个飞手同时抢单）

# ==================== 全局变量 ====================
success_count = 0  # 成功抢单数量
fail_count = 0  # 失败抢单数量
error_count = 0  # 请求异常数量
lock = threading.Lock()  # 线程锁，用于保护共享变量
results = []  # 存储所有请求结果


# ==================== 核心函数 ====================
def accept_mission(pilot_id):
    """
    模拟飞手抢单
    :param pilot_id: 飞手ID
    """
    global success_count, fail_count, error_count
    
    try:
        # 发送抢单请求
        response = requests.post(
            ACCEPT_URL,
            data={
                "missionId": MISSION_ID,
                "pilotId": pilot_id,
                "deviceId": f"UAV{pilot_id:03d}"  # 生成设备ID，如 UAV001, UAV002
            },
            timeout=10  # 10秒超时
        )
        
        # 解析响应
        result = response.json()
        
        with lock:
            if response.status_code == 200 and result.get("code") == 200:
                # 抢单成功
                success_count += 1
                message = f"✅ 飞手 {pilot_id:3d} 抢单成功！"
                print(message)
                results.append({
                    "pilot_id": pilot_id,
                    "status": "SUCCESS",
                    "message": message
                })
            else:
                # 抢单失败（业务逻辑失败）
                fail_count += 1
                error_msg = result.get("message", "未知错误")
                message = f"❌ 飞手 {pilot_id:3d} 抢单失败：{error_msg}"
                print(message)
                results.append({
                    "pilot_id": pilot_id,
                    "status": "FAIL",
                    "message": message
                })
                
    except requests.exceptions.Timeout:
        # 请求超时
        with lock:
            error_count += 1
            message = f"⚠️  飞手 {pilot_id:3d} 请求超时"
            print(message)
            results.append({
                "pilot_id": pilot_id,
                "status": "TIMEOUT",
                "message": message
            })
            
    except Exception as e:
        # 其他异常
        with lock:
            error_count += 1
            message = f"⚠️  飞手 {pilot_id:3d} 请求异常：{str(e)}"
            print(message)
            results.append({
                "pilot_id": pilot_id,
                "status": "ERROR",
                "message": message
            })


def print_banner():
    """打印测试横幅"""
    print("\n" + "="*70)
    print("🚀 无人机任务并发抢单测试")
    print("="*70)
    print(f"📋 测试配置：")
    print(f"   - API 地址：{BASE_URL}")
    print(f"   - 任务 ID：{MISSION_ID}")
    print(f"   - 并发数：{THREAD_COUNT} 个飞手")
    print(f"   - 开始时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("="*70 + "\n")


def print_summary(elapsed_time):
    """打印测试结果摘要"""
    print("\n" + "="*70)
    print("📊 测试结果摘要")
    print("="*70)
    print(f"⏱️  总耗时：{elapsed_time:.2f} 秒")
    print(f"📈 总请求数：{THREAD_COUNT}")
    print(f"✅ 成功数：{success_count}")
    print(f"❌ 失败数：{fail_count}")
    print(f"⚠️  异常数：{error_count}")
    print("="*70)
    
    # 验证结果
    print("\n" + "="*70)
    print("🔍 测试验证")
    print("="*70)
    
    if success_count == 1:
        print("✅ 测试通过！")
        print("   - 只有 1 个飞手抢单成功")
        print("   - Redisson 分布式锁生效")
        print("   - MyBatis-Plus 乐观锁生效")
        print("   - 并发控制正常")
    elif success_count == 0:
        print("⚠️  测试异常！")
        print("   - 没有飞手抢单成功")
        print("   - 可能原因：任务不存在或已被接单")
        print("   - 建议：检查任务ID是否正确，或创建新任务")
    else:
        print("❌ 测试失败！")
        print(f"   - 有 {success_count} 个飞手抢单成功")
        print("   - 存在并发问题！")
        print("   - 分布式锁或乐观锁未生效")
    
    print("="*70)
    
    # 后续操作建议
    print("\n" + "="*70)
    print("📝 后续操作建议")
    print("="*70)
    print("1. 查看数据库验证：")
    print(f"   SELECT * FROM uav_mission WHERE id = {MISSION_ID};")
    print("\n2. 查看应用日志：")
    print("   检查 uav-service 的控制台输出")
    print("\n3. 查看 Redis 锁：")
    print("   redis-cli")
    print(f"   KEYS mission:{MISSION_ID}")
    print("="*70 + "\n")


def main():
    """主函数"""
    # 打印横幅
    print_banner()
    
    # 创建线程列表
    threads = []
    for i in range(1, THREAD_COUNT + 1):
        thread = threading.Thread(target=accept_mission, args=(i,))
        threads.append(thread)
    
    # 记录开始时间
    print(f"🏁 开始并发测试...\n")
    start_time = time.time()
    
    # 同时启动所有线程
    for thread in threads:
        thread.start()
    
    # 等待所有线程完成
    for thread in threads:
        thread.join()
    
    # 记录结束时间
    end_time = time.time()
    elapsed_time = end_time - start_time
    
    # 打印结果摘要
    print_summary(elapsed_time)


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n⚠️  测试被用户中断")
    except Exception as e:
        print(f"\n\n❌ 测试执行出错：{str(e)}")