image_tag <- "jdposa/prone_nlp:0.1"
local_folder <-  "/home/jose/Documents/github_repos/OHDSI_prone"
gcloud_local_folder <- "/home/jose/.config/gcloud"
user <- "ohdsi"
password <- "ohdsi"
forwarded_port <- 9001

bash_string <- sprintf("
sudo docker run -d -v %s:/workdir/workdir -v %s:/workdir/gcloud --name=rstudio-ohdsi -p %s:8787 -e ROOT=TRUE -e USER=%s -e PASSWORD=%s %s",
local_folder,
gcloud_local_folder,
forwarded_port,
user,
password,
image_tag)

print(bash_string)
system(bash_string)