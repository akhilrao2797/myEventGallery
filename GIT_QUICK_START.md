# Quick Git Commands

## Initialize Git Repository (if not done)
git init

## Add all files respecting .gitignore
git add .

## Check what will be committed
git status

## Check what's ignored
git status --ignored

## Make first commit
git commit -m "Initial commit - Event Gallery Platform"

## Add remote (replace with your GitHub URL)
git remote add origin https://github.com/yourusername/myEventGallery.git

## Push to GitHub
git push -u origin main

## Verify repository size
du -sh .git/
