#!/bin/sh

markdown-to-slides slides.md \
  | sed "s/remark.create()/remark.create({highlightStyle: 'tomorrow-night', navigation: { scroll: false }})/g" \
  | sed "s/<\/style>/.w100 img{width:100%;} <\/style>/g" \
  | sed "s/<\/style>/.w90 img{width:90%;} <\/style>/g" \
  | sed "s/<\/style>/.w80 img{width:80%;} <\/style>/g" \
  | sed "s/<\/style>/.w70 img{width:70%;} <\/style>/g" \
  | sed "s/<\/style>/.w60 img{width:60%;} <\/style>/g" \
  | sed "s/<\/style>/.w50 img{width:50%;} <\/style>/g" \
  | sed "s/<\/style>/.w40 img{width:40%;} <\/style>/g" \
  | sed "s/<\/style>/.w25 img{width:25%;} <\/style>/g" \
  > slides.html
