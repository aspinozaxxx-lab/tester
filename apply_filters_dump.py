# -*- coding: utf-8 -*-
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

base_url = 'https://unnd.lab2.pepeshka.ru/'
service = Service(ChromeDriverManager().install())
options = webdriver.ChromeOptions()
options.add_argument('--headless=new')
options.add_argument('--window-size=1920,1080')
driver = webdriver.Chrome(service=service, options=options)
wait = WebDriverWait(driver, 30)

def login():
    driver.get(base_url)
    wait.until(EC.presence_of_element_located((By.ID, 'username')))
    driver.find_element(By.ID, 'username').send_keys('pechenkin')
    driver.find_element(By.ID, 'password').send_keys('Rctybz1988!')
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
    wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'aside.ant-layout-sider')))

login()

driver.get(base_url + 'userTasks')
wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'div.ant-tabs')))
wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "div.ant-tabs-tab[data-node-key='inbox']"))).click()
wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'table tbody tr')))

# apply process filter
def apply_filter(column, value):
    wait.until(EC.element_to_be_clickable((By.XPATH, f"//th[.//span[contains(text(),'{column}')]]//button"))).click()
    wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'div.ant-popover')))
    input_el = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'div.ant-popover input.ant-input')))
    input_el.clear()
    input_el.send_keys(value)
    time.sleep(1)
    btns = driver.find_elements(By.CSS_SELECTOR, 'div.ant-popover button.ant-input-search-button')
    for btn in btns:
        if btn.is_enabled():
            btn.click()
            break
    wait.until(EC.invisibility_of_element_located((By.CSS_SELECTOR, 'div.ant-popover')))

apply_filter('Процесс', 'Регистрация несоответствий сотрудниками АЭС')
apply_filter('Название этапа', 'Оформление несоответствий')

html = driver.page_source
open('after_filters.html','w',encoding='utf-8').write(html)
print('saved after_filters.html')

driver.quit()
