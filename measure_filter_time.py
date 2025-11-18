# -*- coding: utf-8 -*-
import time
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager

BASE_URL = "https://unnd.lab2.pepeshka.ru/"
PROCESS_VALUE = "Ввод нового"
STAGE_VALUE = "Анализ соответствия"

service = Service(ChromeDriverManager().install())
options = webdriver.ChromeOptions()
options.add_argument('--headless=new')
options.add_argument('--window-size=1920,1080')
driver = webdriver.Chrome(service=service, options=options)
wait = WebDriverWait(driver, 30)

def login():
    driver.get(BASE_URL)
    wait.until(EC.presence_of_element_located((By.ID, 'username'))).send_keys('pechenkin')
    wait.until(EC.presence_of_element_located((By.ID, 'password'))).send_keys('Rctybz1988!')
    wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "button[type='submit']"))).click()
    wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'aside.ant-layout-sider')))

def open_my_tasks():
    driver.get(BASE_URL + 'userTasks')
    wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'div.ant-tabs')))
    wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "div.ant-tabs-tab[data-node-key='inbox']"))).click()
    wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'table')))

def apply_filter(column_header, value):
    btn = wait.until(EC.element_to_be_clickable((By.XPATH, f"//th[.//span[contains(normalize-space(.), '{column_header}')]]//button")))
    btn.click()
    popover = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'div.ant-popover')))
    input_el = popover.find_element(By.CSS_SELECTOR, 'input.ant-input')
    input_el.clear()
    input_el.send_keys(value)
    return input_el

try:
    login()
    open_my_tasks()
    apply_filter('Процесс', PROCESS_VALUE).send_keys("\n")
    input_stage = apply_filter('Название этапа', STAGE_VALUE)
    t_start = time.time()
    input_stage.send_keys("\n")

    while True:
        rows = driver.find_elements(By.CSS_SELECTOR, 'table tbody tr')
        empty_block = driver.find_elements(By.CSS_SELECTOR, 'div.ant-empty')
        empty_text = driver.find_elements(By.XPATH, "//*[contains(normalize-space(text()), 'Нет данных')]")
        if rows or empty_block or empty_text:
            t_end = time.time()
            print(f"ready in {t_end - t_start:.2f} seconds")
            break
        time.sleep(0.2)
finally:
    driver.quit()
