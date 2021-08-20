# git 流程

## 分支

- master分支：最终稳定版本，请勿直接将代码提交到该分支。
- dev分支：用于开发，每次都把代码提交到dev分支。

## 流程

1. 本地新建文件夹，cd进入

2. git clone git@gitee.com:colo_ring/one-million.git

3. git branch -v 检查本地分支

4. git branch dev  从master分支新建dev（本地分支名可替换）分支

5. git checkout dev 切换至dev分支

   - 4、5操作可使用 git checkout -b dev 替换
   - *（可使用 git branch -d <分支名> 删除本地分支）*

6. ……修改代码……

7. git add ChangeFile1 ChangedFile2 ……

8. git commit -m "feature update description"

9. git push orign dev

   若本地分支名不是dev，则使用命令

   git push orign 本地分支名:dev

---

后续开发使用 git pull 检查当前分支的更新 ：

git pull origin 远程分支名:本地分支名

