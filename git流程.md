# git 流程

## 分支

- master分支：最终稳定版本，请勿直接将代码提交到该分支。
- dev分支：用于开发，每次都把代码提交到dev分支。

## 流程

1. 本地新建文件夹，cd进入
2. git clone git@gitee.com:colo_ring/one-million.git
3. git branch -v 检查本地分支
4. git branch dev master 从master分支新建dev分支
5. git checkout dev 创建本地分支dev并切换至dev
6. ……修改代码……
7. git add changedFile1 changedFile2 ……
8. git commit -m "description"
9. git push 

